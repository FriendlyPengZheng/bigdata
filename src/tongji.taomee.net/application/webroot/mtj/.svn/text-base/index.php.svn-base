<?php
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', true);
require dirname(dirname(dirname(dirname(__FILE__)))) . '/taomee/taomee.php';
$runtimePath = dirname(dirname(dirname(__FILE__))) . 'runtime' . DS;
$app = TM::createWebApplication(array(
    'import' => array(
        'application.components.*',
        'application.controller.*',
        'application.model.*'
    ),

    'name'              => '数据分析平台',
    'version'           => '1.0',
    'locale'            => 'en_US',
    'supportedLocales'  => ['zh_CN', 'en_US'],
    'systemId'          => 24,
    'systemUrl'         => 'http://tongji.taomee.com/',
    'basePath'          => dirname(dirname(dirname(__FILE__))),
    'defaultSkin'       => 'orange',
    'defaultController' => 'mobile/index',

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
        'user' => array(
            'class'       => 'application.components.MobileUser',
            'powerSystem' => 'http://am-server.taomee.com/index.php',
            'admin'       => array('283', '1097', '1928', '1095', '331', '1061', '1925', '3081','110'),
            'useSSO'      => false,
            'maintain'    => false,
            'additionalAuthority' => array(-3/* for internal games */)
        ),
        'session' => array(
            'name' => 'TMTJMOBILE',
            'maxLifeTime' => 86400
        ),
        'cookie' => array(
            'class' => 'system.web.TMHttpCookie'
        ),
        'cache' => array(
            'class'      => 'system.cache.TMMemcached',
            'open'       => true,
            'servers'    => array(array('127.0.0.1', 11211)),
            'prefix'     => 'tmtj_mobile_',
            'serializer' => 'igbinary'
        ),
        'mailer' => array(
            'class' => 'system.mail.TMMailer',
            'host'  => 'mail.shidc.taomee.com',
            'from'  => array('informplatform@taomee.com' => 'IED-数据平台部')
        )
    )
));
TM::import('application.components.Authorization');
TM::addAuthorization(new Authorization());
$app->run();
