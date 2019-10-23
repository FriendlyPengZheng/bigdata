<?php
class CacheCommand extends TMConsoleCommand
{
    public function actions()
    {
        return array(
            'index'  => true,
            'delete' => true
        );
    }

    public function index($args)
    {
        echo "You are here!\n";
    }

    public function delete($key, $prefix = null)
    {
        $cache = TM::app()->getCache();
        if ($prefix) $cache->prefix = $prefix;
        $cache->delete($key);
        echo "Done!\n";
    }
}
