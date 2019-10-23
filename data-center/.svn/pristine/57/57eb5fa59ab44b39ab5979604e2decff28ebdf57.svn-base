<?php
class StatHttp extends TMComponent
{
    public $url;

    public $defaultParameters = array();

    public $method = 'get';

    public $dataType = 'json';

    public $timeout = 60;

    public function init()
    {
        if (!isset($this->url)) {
            throw new TMException('StatHttp must be initialized with url.');
        }
    }

    public function get($rules, $begin, $end, $interval = 1)
    {
        $rules = array_chunk($rules, 10); // limitation of the http interface.
        $total = array();
        foreach ($rules as $rule) {
            $data = array_merge($this->defaultParameters, array(
                'ids'        => implode(',', $rule),
                'start_time' => $begin,
                'end_time'   => $end,
                'interval'   => $interval
            ));
            $res = TMCurlHelper::fetch($this->url, $data, $this->method, $this->dataType, $this->timeout);
            if (!is_array($res)) throw new TMException('StatHttp fetch data error: ' . $res);
            $total = array_merge($total, $res);
        }
        return $total;
    }
}
