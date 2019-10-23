<?php
$file = $_GET["f"];
$s = socket_create(AF_INET, SOCK_STREAM, getprotobyname('tcp'));
socket_connect($s, '10.1.1.63', 19210);

$len = 12;
$cmd_id = 0x3000;
$file = 1;
$d = pack("LLL", $len, $cmd_id, $file);
socket_send($s, $d, $len, 0);
socket_close($s);
