<?php
/*
 * <code>declare(ticks = 1);</code>
 */
abstract class TMDaemonCommand extends TMConsoleCommand
{
    /**
     * @var string the process pid file.
     */
    public $pidFile = null;

    /**
     * @var integer the max count of worker.
     */
    public $maxWorkerCount = 5;

    /**
     * @var integer the interval in seconds to check if there is a new task.
     */
    public $scanInterval = 10;

    /**
     * @var boolean Whether to keep running or not.
     */
    public $resident = false;

    /**
     * @var integer the process pid.
     */
    protected $pid = null;

    /**
     * @var integer the workers' pids.
     */
    protected $workers = array();

    /**
     * The all actions of the command.
     * Each action must be a method of the command object.
     * @return array
     */
    protected function actions()
    {
        return array(
            'start'  => 1,
            'stop'   => 1,
            'wakeup' => 1
        );
    }

    /**
     * Start this daemon.
     */
    public function start()
    {
        $this->checkEnvironment();
        $this->checkNotRunning();
        $this->daemonize();
        $this->installSignal();
        while (true) {
            $count = count($this->workers);
            if ($count < $this->maxWorkerCount) {
                $this->clean();
                if ($task = $this->getTask()) {
                    $this->beforeWork($task);
                    $pid = pcntl_fork();
                    if ($pid === -1) {
                        $this->log('Couldn\'t fork worker.', 'error');
                        exit(1);
                    } elseif ($pid === 0) {
                        pcntl_signal(SIGCHLD, SIG_DFL);
                        pcntl_signal(SIGTERM, SIG_DFL);
                        pcntl_signal(SIGUSR1, SIG_DFL);
                        pcntl_signal(SIGPIPE, SIG_IGN);
                        $this->clean();
                        if ($this->work($task)) {
                            $this->afterSucceed($task);
                        } else {
                            $this->afterFail($task);
                        }
                        exit(0);
                    } else {
                        $this->workers[$pid] = 1;
                    }
                } elseif ($this->resident) {
                    $this->onNoTask();
                } elseif ($count === 0) {
                    posix_kill($this->pid, SIGTERM);
                }
            }
            sleep($this->scanInterval);
        }
    }

    /**
     * Check if the current environment is suitable for running a daemon.
     */
    public function checkEnvironment()
    {
        if (!isset($this->pidFile)) {
            $this->usageError('You must specify the PID file of the daemon process.');
        }

        if (PHP_SAPI !== 'cli') {
            $this->usageError('The daemon must be run from the command line.');
        }
    }

    /**
     * Check if the daemon has already been running.
     */
    public function checkNotRunning()
    {
        $pid = $this->readPidFile();
        if ($pid !== '' && posix_kill($pid, 0)) {
            posix_kill($pid, SIGUSR1);
            $this->usageError("The daemon has already been running with pid={$pid}.", 0);
        }
    }

    /**
     * Read PID from PID file.
     * @return integer
     */
    public function readPidFile($required = false)
    {
        $pid = '';
        if (file_exists($this->pidFile)) {
            $handler = @fopen($this->pidFile, 'r');
            if ($handler === false) {
                $this->usageError("The PID file\n{$this->pidFile}\ncouldn't be opened.");
            }
            if (flock($handler, LOCK_EX)) {
                $pid = trim(fread($handler, filesize($this->pidFile)));
                flock($handler, LOCK_UN);
                fclose($handler);
            } else {
                $this->usageError("The PID file\n{$this->pidFile}\ncouldn't be locked.");
            }
        } elseif ($required) {
            $this->usageError("The PID file\n{$this->pidFile}\ncouldn't be found.");
        }
        return $pid;
    }

    /**
     * Daemonize the process.
     */
    public function daemonize()
    {
        set_time_limit(0);
        umask(0);
        $pid = pcntl_fork();
        if ($pid === -1) {
            $this->usageError('The process couldn\'t fork.');
        } elseif ($pid !== 0) {
            exit(0);
        }
        if (posix_setsid() === -1) {
            $this->usageError('The process couldn\'t be setted as a session leader.');
        }
        if (!pcntl_signal(SIGHUP, SIG_IGN)) {
            $this->usageError('The signal SIGHUP couldn\'t be ignored.');
        }
        $pid = pcntl_fork();
        if ($pid === -1) {
            $this->usageError('The process couldn\'t fork.');
        } elseif ($pid !== 0) {
            exit(0);
        }
        $this->pid = posix_getpid();
        $this->writePidFile();
        if (!TAOMEE_DEBUG) {
            fclose(STDIN);
            fclose(STDOUT);
            fclose(STDERR);
        }
        $this->log("Daemon started with pid={$this->pid}");
    }

    /**
     * Write current PID to PID file.
     */
    public function writePidFile()
    {
        $handler = @fopen($this->pidFile, 'w');
        if ($handler === false) {
            $this->usageError("The PID file\n{$this->pidFile}\ncouldn't be opened.");
        }
        if (flock($handler, LOCK_EX)) {
            $count = fwrite($handler, $this->pid);
            flock($handler, LOCK_UN);
            fclose($handler);
            if ($count === false) {
                $this->usageError("The daemon PID couldn't be written into the PID file\n{$this->pidFile}");
            }
        } else {
            $this->usageError("The PID file\n{$this->pidFile}\ncouldn't be locked.");
        }
    }

    /**
     * Install handlers for signals.
     */
    public function installSignal()
    {
        pcntl_signal(SIGCHLD, array($this, 'workerExitHandler'));
        pcntl_signal(SIGTERM, array($this, 'daemonExitHandler'));
        pcntl_signal(SIGUSR1, array($this, 'daemonWakeupHandler'));
    }

    /**
     * Handle something when the worker exits.
     */
    public function workerExitHandler()
    {
        if ($this->pid === posix_getpid()) {
            while(($pid = pcntl_waitpid(-1, $status, WNOHANG)) > 0) {
                unset($this->workers[$pid]);
            }
        }
    }

    /**
     * Handle something when the daemon exits.
     */
    public function daemonExitHandler()
    {
        if ($this->pid === posix_getpid()) {
            foreach ($this->workers as $pid => $info) {
                if (!posix_kill($pid, SIGTERM) && posix_kill($pid, 0)) {
                    $this->log("Failed to kill worker with pid={$pid}", 'error');
                }
            }
            if (file_exists($this->pidFile)) {
                unlink($this->pidFile);
            }
            exit(1);
        }
    }

    /**
     * Handler used to wake up the daemon, actually do nothing.
     */
    public function daemonWakeupHandler()
    {
        // Just used to wake up the daemon.
    }

    /**
     * Get a task.
     * @return array
     */
    abstract protected function getTask();

    /**
     * Work.
     * @param array $task
     */
    abstract protected function work($task);

    /**
     * Callback before work.
     * @param array $task
     */
    protected function beforeWork($task)
    {
    }

    /**
     * Callback when the work succeeds.
     * @param array $task
     */
    protected function afterSucceed($task)
    {
    }

    /**
     * Callback when the work fails.
     * @param array $task
     */
    protected function afterFail($task)
    {
    }

    /**
     * Call when there is no task any more.
     */
    protected function onNoTask()
    {
    }

    /**
     * Clean before get or do a task, such as the database connection.
     */
    protected function clean()
    {
    }

    /**
     * Stop the daemon.
     */
    public function stop()
    {
        $pid = $this->readPidFile(true);
        if ($pid !== '' && posix_kill($pid, 0)) {
            posix_kill($pid, SIGTERM);
        }
        echo "Stopped.\n";
    }

    /**
     * Wake up the daemon.
     */
    public function wakeup()
    {
        $pid = $this->readPidFile(true);
        if ($pid !== '' && posix_kill($pid, 0)) {
            posix_kill($pid, SIGUSR1);
        }
        echo "Woken up.\n";
    }
}
