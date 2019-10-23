#!/usr/bin/php
<?php
echo 'begin ...' . "\n";
$monitor = new SoapClient('./monitor.wsdl', 
			              array('login' => 'stat',
				                'password' => 'taomee',
								'cache_wsdl' => WSDL_CACHE_NONE));
print_r($monitor->__getFunctions());
//echo $monitor->getClientInfo();
//echo $monitor->__soapCall('getClientInfo', array());
//echo $monitor->getClientInfo();
//echo $monitor->echo();
//echo $monitor->getClientInfo();
print_r($monitor->__soapCall('get-server-info', array()));
print_r($monitor->__soapCall('get-client-info', array()));
echo 'end ...' . "\n";
?>
