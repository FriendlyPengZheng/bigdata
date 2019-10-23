<?php

date_default_timezone_set('Asia/Shanghai');

class Log {

    private $s_log_dir = "log/";
    private $s_log_prefix = "";
    private $r_debug = false;
    private $r_error = false;
    private $i_last_time = 0;

    public function __construct($s_log_prefix) {
        if(!file_exists($this->s_log_dir)) {
            mkdir($this->s_log_dir, 0777);
        }

        $this->s_log_prefix = $s_log_prefix;
    }

    public function __destruct() {
        $this->close_file();
    }

    private function open_file($type) {
        $this->i_last_time = date("Ymd", time());
        $type === 0 ?
            $this->r_debug = fopen($this->s_log_dir . $this->s_log_prefix . "debug" . $this->i_last_time, 'a') : 
            $this->r_error = fopen($this->s_log_dir . $this->s_log_prefix . "error" . $this->i_last_time, 'a');
    }

    private function close_file() {
        if($this->r_debug !== false) {
            fclose($this->r_debug);
            $this->r_debug = false;
        }
        if($this->r_error !== false) {
            fclose($this->r_error);
            $this->r_error = false;
        }
    }

    public function debug_log($s_log) {
        $this->write_log($s_log, 0);
    }

    public function error_log($s_log) {
        $this->write_log($s_log, 1);
    }

    private function write_log($s_log, $type) {
        if(date("Ymd", time()) !== $this->i_last_time) {
            $this->close_file();
            $this->open_file($type);
        }

        if($type === 0) {
            if($this->r_debug === false)
                $this->open_file($type);
            fwrite($this->r_debug, date("[Y-m-d H:m:s]") . $s_log . "\n");
        } else {
            if($this->r_error === false)
                $this->open_file($type);
            fwrite($this->r_error, date("[Y-m-d H:m:s]") . $s_log . "\n");
        }
    }

}
?>
