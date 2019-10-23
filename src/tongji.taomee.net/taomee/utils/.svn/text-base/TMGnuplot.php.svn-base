<?php
/*
 * TMGnuplot class.
 */
class TMGnuplot extends TMComponent
{
    /**
     * @var string The gnuplot command to invoke.
     */
    public $command = '/usr/bin/gnuplot';

    /**
     * @var string Temporary directory for images.
     */
    public $tempDir = null;

    /**
     * @var string Font face.
     */
    public $fontFace = 'WenQuanYi Zen Hei';

    /**
     * @var integer Font size.
     */
    public $fontSize = 10;

    /**
     * @var string RGB color for the background.
     */
    public $backgroundColor = '#ffffff';

    /**
     * @var string width,height
     */
    public $dimension = '1000,400';

    /**
     * @var string t,r,b,l
     */
    public $margin = null;

    /**
     * @var array Predefined line colors.
     */
    public $lineColors = array('#176ded', '#d23e29', '#009555');

    /**
     * @var integer Line width.
     */
    public $lineWidth = 2;

    /**
     * @var resource The gnuplot process handler.
     */
    private $_process = null;

    /**
     * @var resource The input stream of the gnuplot process.
     */
    private $_input = null;

    /**
     * @var resource The output stream of the gnuplot process.
     */
    private $_output = null;

    /**
     * @var resource The error stream of the gnuplot process.
     */
    private $_error = null;

    /**
     * @var integer The current line style.
     */
    private $_currentLineStyle = 1;

    /**
     * @var integer The count of the predefined line styles.
     */
    private $_lineStyleCount = null;

    /**
     * @var array The current canvas.
     */
    private $_canvas = array();

    /**
     * @var integer The count of getting the same unique id.
     */
    private $_conflictCount = 0;

    /**
     * Init TMGnuplot component.
     */
    public function init()
    {
        if (!isset($this->command)) {
            throw new TMGnuplotException(TM::t('taomee', '须指定Gnuplot路径！'));
        }
        if (!isset($this->tempDir)) {
            throw new TMGnuplotException(TM::t('taomee', '须指定缓存路径！'));
        }
        $this->tempDir = rtrim($this->tempDir, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR;
        TMFileHelper::mkdir($this->tempDir);
    }

    /**
     * Create a canvas.
     * @param string $title
     * @return TMGnuplot
     */
    public function createCanvas($title = null)
    {
        $this->_currentLineStyle = 1;
        $this->_canvas = array('id' => $this->_getUniqueId());
        if (isset($title)) {
            $this->_canvas['title'] = TMFileHelper::sanitizeFilename($title);
        }
        return $this;
    }

    /**
     * Set x data.
     * @param array $data
     * @return TMGnuplot
     */
    public function setXData($data)
    {
        if (isset($this->_canvas)) {
            $this->_canvas['xdata'] = $data;
        }
        return $this;
    }

    /**
     * Add a set of y data.
     * @param string $title
     * @param array $data
     * @return TMGnuplot
     */
    public function addYData($title, $data)
    {
        if (isset($this->_canvas)) {
            if (!isset($this->_canvas['ydata'])) {
                $this->_canvas['ydata'] = array();
            }
            $this->_canvas['ydata'][] = array(TMFileHelper::sanitizeFilename($title), $data);
        }
        return $this;
    }

    /**
     * Set x range.
     * @param number $min
     * @param number $max
     * @return TMGnuplot
     */
    public function setXRange($min, $max)
    {
        if (isset($this->_canvas)) {
            $this->_canvas['xrange'] = array($min, $max);
        }
        return $this;
    }

    /**
     * Set xtics add.
     * @param integer $interval
     * @return TMGnuplot
     */
    public function setXticsAdd($interval)
    {
        if (isset($this->_canvas)) {
            $this->_canvas['xtics_add'] = $interval;
        }
        return $this;
    }

    /**
     * set x format.
     * @param string $format
     * @param boolean $time
     * @param string $timeFormat
     * @return TMGnuplot
     */
    public function setXFormat($format, $time = false, $timeFormat = '')
    {
        if (isset($this->_canvas)) {
            $this->_canvas['xformat'] = array($format, $time, $timeFormat);
        }
        return $this;
    }

    /**
     * set y format.
     * @param string $format
     * @param boolean $time
     * @param string $timeFormat
     * @return TMGnuplot
     */
    public function setYFormat($format, $time = false, $timeFormat = '')
    {
        if (isset($this->_canvas)) {
            $this->_canvas['yformat'] = array($format, $time, $timeFormat);
        }
        return $this;
    }

    /**
     * Plot against the current canvas.
     * @return string
     */
    public function plot()
    {
        $dataFile = $this->_writeDataFile();

        $pngFile = $this->tempDir . $this->_canvas['id'] . '.png';
        $this->_start()->_prepare()->_executeCommand("set output \"{$pngFile}\"");

        if (isset($this->_canvas['title'])) {
            $this->_executeCommand("set title \"{$this->_canvas['title']}\"");
        }
        $this->_setAxes();

        $command = "plot \"{$dataFile}\"";
        foreach ($this->_canvas['ydata'] as $i => $y) {
            if ($i > 0) {
                $command .= ',""';
            }
            $column = $i + 2;
            $command .= " using 1:{$column} title \"{$y[0]}\" with lines ls {$this->_getLineStyle()}";
        }
        $this->_executeCommand($command);

        fclose($this->_input);
        fclose($this->_output);

        $error = stream_get_contents($this->_error);
        fclose($this->_error);

        if ($code = proc_close($this->_process)) {
            throw new TMGnuplotException(TM::t('taomee', '绘图错误，状态码{code}，描述{error}！',
                array('{code}' => $code, '{error}' => $error)));
        }

        @unlink($dataFile);
        return $pngFile;
    }

    /**
     * Destructor.
     */
    public function __destruct()
    {
        if (is_resource($this->_input)) {
            fclose($this->_input);
        }
        if (is_resource($this->_output)) {
            fclose($this->_output);
        }
        if (is_resource($this->_error)) {
            fclose($this->_error);
        }
        if (is_resource($this->_process)) {
            proc_close($this->_process);
        }
    }

    /**
     * Set axes.
     * @return TMGnuplot
     */
    private function _setAxes()
    {
        // X format
        if (isset($this->_canvas['xformat'])) {
            if ($this->_canvas['xformat'][1]) {
                $this->_executeCommand('set xdata time');
                $this->_executeCommand("set timefmt \"{$this->_canvas['xformat'][2]}\"");
            }
            $this->_executeCommand("set format x \"{$this->_canvas['xformat'][0]}\"");
        }
        // Y format
        if (isset($this->_canvas['yformat'])) {
            if ($this->_canvas['yformat'][1]) {
                $this->_executeCommand('set ydata time');
                $this->_executeCommand("set timefmt \"{$this->_canvas['yformat'][2]}\"");
            }
            $this->_executeCommand("set format y \"{$this->_canvas['yformat'][0]}\"");
        }
        // X range
        if (isset($this->_canvas['xrange'])) {
            $this->_executeCommand("set xrange [\"{$this->_canvas['xrange'][0]}\":\"{$this->_canvas['xrange'][1]}\"]");
        } else {
            $this->_executeCommand("set xrange [\"{$this->_canvas['xdata'][0]}\":\"{$this->_canvas['xdata'][count($this->_canvas['xdata']) - 1]}\"]");
        }
        // Xtics add
        if (isset($this->_canvas['xtics_add'])) {
            $this->_executeCommand("set xtics add {$this->_canvas['xtics_add']}");
        }

        return $this;
    }

    /**
     * Start the gnuplot process.
     * @return TMGnuplot
     */
    private function _start()
    {
        $descriptorspec = array(
            0 => array('pipe', 'r'),
            1 => array('pipe', 'w'),
            2 => array('pipe', 'w')
        );

        $pipes = array();
        $this->_process = proc_open($this->command, $descriptorspec, $pipes);
        if (!is_resource($this->_process)) {
            throw new TMGnuplotException(TM::t('taomee', '启动{command}错误！', array('{command}' => $this->command)));
        }

        $this->_input  = $pipes[0];
        $this->_output = $pipes[1];
        $this->_error  = $pipes[2];

        return $this;
    }

    /**
     * Prepare for plotting, sucn as line styles.
     * @return TMGnuplot
     */
    private function _prepare()
    {
        // Line styles.
        foreach ($this->lineColors as $i => $color) {
            $num = $i + 1;
            $this->_executeCommand("set style line {$num} lw {$this->lineWidth} lc rgb \"{$color}\"");
        }
        $this->_lineStyleCount = count($this->lineColors);

        // Terminal
        $command = 'set terminal pngcairo color notransparent nocrop';
        if (isset($this->backgroundColor)) {
            $command .= " background \"{$this->backgroundColor}\"";
        }
        if (($font = $this->_getFont()) !== false) {
            $command .= " font \"{$font}\"";
        }
        if (isset($this->dimension)) {
            $command .= " size {$this->dimension}";
        }
        $this->_executeCommand($command);

        // Margin
        if (isset($this->margin)) {
            $margin = explode(',', $this->margin);
            if (count($margin) === 4) {
                $this->_executeCommand("set tmargin {$margin[0]}");
                $this->_executeCommand("set rmargin {$margin[1]}");
                $this->_executeCommand("set bmargin {$margin[2]}");
                $this->_executeCommand("set lmargin {$margin[3]}");
            }
        }

        // Common
        $this->_executeCommand('set datafile separator ","');
        $this->_executeCommand('set grid xtics ytics mxtics mytics front');
        $this->_executeCommand("set xtics rangelimited");
        $this->_executeCommand('set key outside center bottom horizontal Left reverse');

        return $this;
    }

    /**
     * Get font.
     * @return string|false
     */
    private function _getFont()
    {
        if (isset($this->fontFace, $this->fontSize)) {
            return $this->fontFace . ',' . $this->fontSize;
        }
        return false;
    }

    /**
     * Get one line style number.
     * @return integer|false
     */
    private function _getLineStyle()
    {
        if ($this->_lineStyleCount > 0) {
            $number = $this->_currentLineStyle;
            if ($number > $this->_lineStyleCount) {
                $number = $this->_currentLineStyle = 1;
            }
            $this->_currentLineStyle += 1;
            return $number;
        }
        return false;
    }

    /**
     * Write data file against the current canvas.
     * @return string
     */
    private function _writeDataFile()
    {
        $dataFile = $this->tempDir . $this->_canvas['id'] . '.dat';
        if (file_exists($dataFile)) {
            if ($this->_conflictCount++ > 5) {
                throw new TMGnuplotException(TM::t('taomee', '唯一键冲突次数过多！'));
            }
            $this->_canvas['id'] = $this->_getUniqueId();
            $this->_writeDataFile();
        }
        $this->_conflictCount = 0;

        if (!isset($this->_canvas['xdata']) || !isset($this->_canvas['ydata'])) {
            throw new TMGnuplotException(TM::t('taomee', '须提供绘图数据！'));
        }
        if (($handle = fopen($dataFile, 'w')) === false) {
            throw new TMGnuplotException(TM::t('taomee', '数据文件{file}不可写入！', array('{file}' => $dataFile)));
        }
        foreach ($this->_canvas['xdata'] as $i => $x) {
            $data = array($x);
            foreach ($this->_canvas['ydata'] as $y) {
                $data[] = isset($y[1][$i]) ? $y[1][$i] : 0;
            }
            if (fputcsv($handle, $data) === false) {
                throw new TMGnuplotException(TM::t('taomee', '数据文件写入错误！'));
            }
        }

        fclose($handle);
        return $dataFile;
    }

    /**
     * Run a command against the input stream.
     * @param string $command
     */
    private function _executeCommand($command)
    {
        if (fwrite($this->_input, $command . "\n") === false) {
            throw new TMGnuplotException(TM::t('taomee', '运行{command}错误！', array('{command}' => $command)));
        }
    }

    /**
     * Get unique id for temp file.
     * @return string
     */
    private function _getUniqueId()
    {
        return md5(uniqid('', true));
    }
}

/*
 * TMGnuplotException class.
 */
class TMGnuplotException extends TMException
{
}
