<?php
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', false);
require dirname(dirname(dirname(__FILE__))) . '/taomee/taomee.php';
TM::createWebApplication(array(
    'import' => array(
        'application.controller.*',
        'application.model.*'
    ),

    'name'      => '数据分析平台',
    'version'   => '1.0.3161',
    'locale'    => 'zh_CN',
    'systemId'  => 24,
    'systemUrl' => 'http://tongji.taomee.com/',
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
            'connectionString' => 'mysql:host=192.168.71.76;port=3306;dbname=db_td_config',
            'charset'          => 'utf8',
            'username'         => 'tdconfig',
            'password'         => 'tdconfig@pwd123'
        ),
        'rDb' => array(
            'class' => 'system.db.TMDbConnection',
            'connectionString' => 'mysql:host=192.168.71.43;port=3307;dbname=db_td_config',
            'charset'          => 'utf8',
            'username'         => 'tdconfig',
            'password'         => '43oOq8Uj8Ci'
        ),
        'navigator' => array(
            'class' => 'application.components.Navigator'
        ),
        'sso' => array(
            'loginUrl'  => 'https://home.taomee.com/index.php',
            'powerUrl'  => 'https://home.taomee.com/api.php'
        ),
        'user' => array(
            'powerSystem' => 'https://ams.taomee.com/',
            'admin'       => array(
                1574/* violet */,110
            ),
            'useSSO'      => true,
            'maintain'    => false
        ),
        'template' => array(
            'forceCompile' => false,
            'forceCache'   => false
        ),
        'session' => array(
        'name' => 'TMTJMAIN',
        'saveHandler' => 'memcached',
        'savePath' => '127.0.0.1:11211'
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
