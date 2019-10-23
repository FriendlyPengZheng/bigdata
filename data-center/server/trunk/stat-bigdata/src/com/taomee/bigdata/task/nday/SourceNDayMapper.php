<?php
$n = $argv[1];
$classname = "SourceNDay{$n}Mapper";
$fd = fopen("{$classname}.java", "w");
fwrite($fd, "package com.taomee.bigdata.task.nday;\n");
fwrite($fd, "\n");
fwrite($fd, "import org.apache.hadoop.io.*;\n");
fwrite($fd, "import org.apache.hadoop.mapred.*;\n");
fwrite($fd, "import org.apache.hadoop.mapred.lib.*;\n");
fwrite($fd, "\n");
fwrite($fd, "import java.io.*;\n");
fwrite($fd, "import com.taomee.bigdata.task.common.SourceSetMapper;\n");
fwrite($fd, "\n");
fwrite($fd, "public class {$classname} extends SourceSetMapper\n");
fwrite($fd, "{\n");
fwrite($fd, "    public void configure(JobConf job) {\n");
fwrite($fd, "        outputValue.set({$n});\n");
fwrite($fd, "        super.configure(job);\n");
fwrite($fd, "    }\n");
fwrite($fd, "}\n");
?>
