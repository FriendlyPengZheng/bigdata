<?php
class ProtoCommand extends TMConsoleCommand
{
    public function actions()
    {
        return array(
            'index'  => true,
            'gen' => true
        );
    }

    public function gen($src, $dst)
    {
        TMProto::register();
        $parser = new TMProtoParser();
        $compiler = new TMProtoCompiler();
        file_put_contents($dst, $compiler->compile($parser->parse(file_get_contents($src))));
    }
}
