<?php
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', true);
require '../../taomee/taomee.php';
TM::createWebApplication(array(
    'import' => array(
        'application.model.*'
    ),

    'name'      => '在线统计平台',
    'version'   => '1.0',
    'locale'    => 'zh_CN',
    'systemId'  => 12,
    'systemUrl' => 'http://stat-online.taomee.net/',
    'basePath'  => dirname(dirname(__FILE__)),
    'defaultSkin' => 'orange',
    'defaultController' => 'online',

    'routeMapping' => array(
        'user' => array()
    ),

    'components' => array(
        'errorHandler' => array(
            'errorAction' => 'error/showError'
        ),
        'db' => array(
            'connectionString' => 'mysql:host=10.1.1.60;port=3306;dbname=db_td_config',
            'charset'          => 'utf8',
            'username'         => 'root',
            'password'         => 'pwd@60'
        ),
        'user' => array(
            'powerSystem'        => 'http://10.1.1.27/am/service/index.php',
            'useSSO'             => false,
            'localIp'            => array('10.1.2.46', '10.1.2.61'),
            'foreignerAuthority' => '12069940016',
            'game'               => dirname(dirname(__FILE__)) . DS . 'config' . DS . 'game.php'
        ),
        'session' => array(
            'name' => 'TMOL'
        ),
        'cache' => array(
            'class'      => 'system.cache.TMMemcached',
            'open'       => true,
            'servers'    => array(array('127.0.0.1', 11211)),
            'prefix'     => 'tmol_',
            'serializer' => 'igbinary'
        ),
        'statHttp' => array(
            'class'             => 'application.components.StatHttp',
            'url'               => 'http://192.168.71.57/lock-db/api-online/api.php',
            'defaultParameters' => array('action' => 'getAvgData')
        ),
        'socket' => array(
            'class'   => 'system.utils.TMSocket',
            'address' => '192.168.71.37',
            'port'    => 19906,
            'timeout' => 5
        )
    )
))->run();
