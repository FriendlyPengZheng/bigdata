<?php
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', true);
defined('IS_DEBUG') or define('IS_DEBUG', true);
require dirname(dirname(dirname(__FILE__))) . '/taomee/taomee.php';
TM::createWebApplication(array(
    'import' => array(
        'application.controller.*',
        'application.model.*'
    ),

    'name'      => '数据分析平台',
    'version'   => '1.0',
    'locale'    => 'zh_CN',
    'systemId'  => 24,
    'systemUrl' => 'http://tongji.taomee.net/',
    'basePath'  => dirname(dirname(__FILE__)),
    'defaultSkin' => 'orange',
    'defaultController' => 'gameanalysis/webgame/overview',

    'routeMapping' => array(
        'user' => array()
    ),

    'components' => array(
        'errorHandler' => array(
            'errorAction' => 'site/error/showError'
        ),
        'db' => array(
            'connectionString' => 'mysql:host=10.1.1.60;port=3306;dbname=db_td_config',
            'charset'          => 'utf8',
            'username'         => 'root',
            'password'         => 'pwd@60'
        ),
        'rDb' => array(
            'class' => 'system.db.TMDbConnection',
            'connectionString' => 'mysql:host=10.1.1.60;port=3306;dbname=db_td_config',
            'charset'          => 'utf8',
            'username'         => 'root',
            'password'         => 'pwd@60'
        ),
        'navigator' => array(
            'class' => 'application.components.Navigator'
        ),
        'sso' => array(
            'loginUrl'  => 'http://home.taomee.net/index.php',
            'powerUrl'  => 'http://home.taomee.net/api.php'
        ),
        'user' => array(
            'powerSystem' => 'http://10.1.1.27/am/service/index.php',
            'admin'       => array('1574', '2375', '2409','110'),
            'useSSO'      => true,
            'maintain'    => false
        ),
        'session' => array(
            'name' => 'TMTJMAIN'
        ),
        'cache' => array(
            'class'      => 'system.cache.TMMemcached',
            'open'       => true,
            'servers'    => array(array('127.0.0.1', 11211)),
            'prefix'     => 'tmtj_',
            'serializer' => 'igbinary'
        ),
        'mailer' => array(
            'class' => 'system.mail.TMMailer',
            'host'  => 'mail.shidc.taomee.com',
            'from'  => array('informplatform@taomee.com' => 'IED-数据平台部')
        ),
        'plot' => array(
            'class'   => 'system.utils.TMGnuplot',
            'backgroundColor' => '#E8F8F7',
            'tempDir' => dirname(dirname(__FILE__)) . DS . 'runtime' . DS . 'temp'
        )
    )
))->run();
