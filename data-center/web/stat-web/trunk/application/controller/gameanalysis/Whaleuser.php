<?php
abstract class gameanalysis_Whaleuser extends gameanalysis_Analysis
{
    public function actions()
    {
        return array_merge(parent::actions(), array(
            'getData' => array(
                'period' => 3,
                'top'    => 500
            ),
            'getInfo' => array(
                'account_id' => null
            )
        ));
    }

    public function getData($aUserParameters)
    {
        $aUserParameters['time'] = $this->getTime($aUserParameters);
        $model = new gameanalysis_WhaleUserData();
        $list = $model->getList($aUserParameters);
        $accumulator = 0;
        foreach ($list as $idx => &$data) {
            $data['top'] = $idx + 1;
            $data['ctime'] = date('Y-m-d', $data['ctime']);
            $data['total_payments'] = round($data['total_payments']/100, 2);
            $data['total_ratio'] = round($data['total_ratio'], 6);
            $accumulator += $data['total_ratio'];
            $data['total_ratio'] .= '%';
            $data['accu_ratio'] = $accumulator . '%';
        }
        if ($aUserParameters['export']) $this->export($list, $aUserParameters);
        $this->ajax(0, $list);
    }

    public function getInfo($aUserParameters)
    {
        $model = new gameanalysis_WhaleUserInfo();
        $list = $model->getList($aUserParameters);
        if (!$list) $this->ajax(0);

        $list[0]['first_buyitem_time']   = $list[0]['first_buyitem_time'] ? date('Y-m-d', $list[0]['first_buyitem_time']) : '-';
        $list[0]['last_buyitem_time']    = $list[0]['last_buyitem_time'] ? date('Y-m-d', $list[0]['last_buyitem_time']) : '-';
        $list[0]['buyitem_total_amount'] = round($list[0]['buyitem_total_amount']/100, 2);

        $list[0]['vip'] = $list[0]['vip'] ? 'yes' : 'no';
        $list[0]['last_login_time']  = $list[0]['last_login_time'] ? date('Y-m-d', $list[0]['last_login_time']) : '-';
        $list[0]['first_vip_time']   = $list[0]['first_vip_time'] ? date('Y-m-d', $list[0]['first_vip_time']) : '-';
        $list[0]['last_vip_time']    = $list[0]['last_vip_time'] ? date('Y-m-d', $list[0]['last_vip_time']) : '-';
        $list[0]['vip_total_amount'] = round($list[0]['vip_total_amount']/100, 2);

        $this->ajax(0, $list[0]);
    }

    protected function getTime($aUserParameters)
    {
        if (is_array($aUserParameters['from'])) {
            $aUserParameters['from'] = array_pop($aUserParameters['from']);
        }
        $aUserParameters['to'] = $aUserParameters['from'];
        $period = data_time_PeriodFactory::createPeriod(
            $aUserParameters['from'],
            $aUserParameters['to'],
            $aUserParameters['period']
        );
        return $period->getFrom();
    }

    protected function export($list, $aUserParameters)
    {
        $this->initExporter($aUserParameters)->add($this->sFilename);
        $head = $this->getDataHead();
        $this->oExporter->put(array_values($head), CsvExporter::ENCODE_ALL);
        foreach ($list as $data) {
            $line = array();
            foreach ($head as $field => $name) {
                $line[] = $data[$field];
            }
            $this->oExporter->put($line, CsvExporter::ENCODE_PREV, 2);
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    protected function getDataHead()
    {
        return array(
            'top'            => 'Top',
            'platform_name'  => TM::t('tongji', '平台'),
            'account_id'     => TM::t('tongji', '帐号'),
            'ctime'          => TM::t('tongji', '新增日期'),
            'current_level'  => TM::t('tongji', '当前等级'),
            'total_payments' => TM::t('tongji', '月付费总额'),
            'total_count'    => TM::t('tongji', '月付费次数'),
            'total_ratio'    => TM::t('tongji', '月付费额占比'),
            'accu_ratio'     => TM::t('tongji', '累计占比')
        );
    }
}
