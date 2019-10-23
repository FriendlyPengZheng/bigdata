<?php
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', true);
require dirname(dirname(dirname(__FILE__))) . '/taomee/taomee.php';
$app = TM::createWebApplication(array(
    'import' => array(
        'application.controller.*',
        'application.model.*'
    ),

    'name'              => '数据分析平台',
    'version'           => '1.0',
    'locale'            => 'zh_CN',
    'supportedLocales'  => ['zh_CN', 'en_US'],
    'systemId'          => 24,
    'systemUrl'         => 'http://tongji.taomee.net/',
    'basePath'          => dirname(dirname(__FILE__)),
    'defaultSkin'       => 'orange',
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
            'class'     => 'system.web.TMSSOClient',
            'loginUrl'  => 'http://home.taomee.net/index.php',
            'powerUrl'  => 'http://home.taomee.net/api.php'
        ),
        'user' => array(
            'class'       => 'application.components.User',
            'powerSystem' => 'http://am-server.taomee.net/index.php',
            'admin'       => array('1924','283', '1097', '1928', '1095', '331', '1061', '1925', '3081','110'),
            'useSSO'      => false,
            'maintain'    => false,
            'additionalAuthority' => array(-3/* for internal games */)
        ),
        'session' => array(
            'name' => 'TMTJMAIN'
        ),
        'cookie' => array(
            'class' => 'system.web.TMHttpCookie'
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
        ),
        'backendSocket' => array(
            'class'   => 'system.utils.TMSocket',
            'address' => '10.1.1.44',
            'port'    => 19300,
            'timeout' => 5
        ),
        'MonthExcel' => array(
            'class' => 'system.excel.TMExcel',
            'defaultDirname' => dirname(dirname(__FILE__)) . DS . 'runtime' . DS . 'files' . DS . 'month',
            'creator' => 'tongji.taomee.com',
            'lastModifiedBy' => 'tongji.taomee.com'
        ),
        'translator' => array(
            'record'     => true,
            'recordPath' => dirname(dirname(__FILE__)) . DS . 'runtime' . DS . 'temp'
        )
    )
));
TM::import('application.components.Authorization');
TM::import('application.components.AutoDisplay');
TM::addAuthorization(new Authorization());
$app->setAutoDisplay(new AutoDisplay());
$app->run();
