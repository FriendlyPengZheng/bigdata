<?php
$n = $argv[1];
$classname = "NDay{$n}Mapper";
$fd = fopen("{$classname}.java", "w");
fwrite($fd, "package com.taomee.bigdata.task.nday;\n");
fwrite($fd, "\n");
fwrite($fd, "import org.apache.hadoop.io.*;\n");
fwrite($fd, "import org.apache.hadoop.mapred.*;\n");
fwrite($fd, "import org.apache.hadoop.mapred.lib.*;\n");
fwrite($fd, "\n");
fwrite($fd, "import java.io.*;\n");
fwrite($fd, "import com.taomee.bigdata.task.common.SetMapper;\n");
fwrite($fd, "\n");
fwrite($fd, "public class {$classname} extends SetMapper\n");
fwrite($fd, "{\n");
fwrite($fd, "    public void configure(JobConf job) {\n");
fwrite($fd, "        outputValue.set({$n});\n");
fwrite($fd, "        super.configure(job);\n");
fwrite($fd, "    }\n");
fwrite($fd, "}\n");
fclose($fd);
?>
