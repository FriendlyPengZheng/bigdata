<?php
declare(ticks = 1); // if not daemon, comment this line.
defined('TAOMEE_DEBUG') or define('TAOMEE_DEBUG', true);
require dirname(dirname(dirname(__FILE__))) . '/taomee/taomee.php';
TM::createConsoleApplication(array(
    'import' => array(
        'application.model.*'
    ),

    'name'     => '数据分析平台',
    'version'  => '1.0',
    'locale'   => 'zh_CN',
    'basePath' => dirname(dirname(__FILE__)),

    'components' => array(
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
            'from'  => array('informplatform@taomee.com' => 'IED-数据平台部'),
            'cc'    => array('violet@taomee.com')
        ),
        'plot' => array(
            'class'   => 'system.utils.TMGnuplot',
            'backgroundColor' => '#E8F8F7',
            'tempDir' => dirname(dirname(__FILE__)) . DS . 'runtime' . DS . 'temp'
        ),
        'template' => array(
            'class'       => 'system.web.template.TMTemplateAdapter',
            'templateDir' => dirname(dirname(__FILE__)) . DS . 'view',
            'compileDir'  => dirname(dirname(__FILE__)) . DS . 'runtime' . DS . 'compiles',
            'cacheDir'    => dirname(dirname(__FILE__)) . DS . 'runtime' . DS . 'caches'
        )
    )
))->run();
