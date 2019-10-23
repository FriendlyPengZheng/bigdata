<?php
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', false);
require '../../taomee/taomee.php';
TM::createWebApplication(array(
    'import' => array(
        'application.model.*'
    ),

    'name'      => '在线统计平台',
    'version'   => '1.0',
    'locale'    => 'zh_CN',
    'systemId'  => 12,
    'systemUrl' => 'https://stat-online.taomee.com/',
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
            'connectionString' => 'mysql:host=192.168.71.76;port=3306;dbname=db_td_config',
            'charset'          => 'utf8',
            'username'         => 'tdconfig',
            'password'         => 'tdconfig@pwd123'
        ),
        'user' => array(
            'class'              => 'application.components.User',
            'powerSystem'        => 'https://ams.taomee.com/',
            'useSSO'             => false,
            'localIp'            => '116.228.240.106',
            'foreignerAuthority' => '12069940016',
            'trace'              => true,
            'game'               => dirname(dirname(__FILE__)) . DS . 'config' . DS . 'game.php'
        ),
        'template' => array(
            'forceCompile' => false,
            'forceCache'   => false
        ),
        'session' => array(
            'name' => 'TMOL',
            'saveHandler' => 'memcached',
            'savePath' => '127.0.0.1:11211',
            'maxLifeTime' => 86400
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
