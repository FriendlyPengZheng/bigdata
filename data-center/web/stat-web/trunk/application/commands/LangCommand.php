<?php
class LangCommand extends TMConsoleCommand
{
    public function actions()
    {
        return array(
            'index'  => true,
            'import' => true,
            'export' => true
        );
    }

    public function import($mask, $lang, $src)
    {
        $oLangEntry = new tool_LangEntry();
        $t = include($src);
        $slot = 1 << $mask;
        foreach ($t as $source => $translation) {
            $oLangEntry->getDb()->createCommand(
                'INSERT INTO ' . $oLangEntry->tableName() . '(source,translation,lang,category_slot) ' .
                'VALUES (?,?,?,?) ' .
                'ON DUPLICATE KEY UPDATE translation=?,category_slot=category_slot|?'
            )->execute([$source, $translation, $lang, $slot, $translation, $slot]);
        }

        echo "完成！\n";
    }

    public function export($mask, $dst)
    {
        $mask = (int)$mask;
        $oLangEntry = new tool_LangEntry();
        $aEntries = $oLangEntry->findByCategoryMask($mask);
        foreach ($aEntries as $lang => $entry) {
            $t = [];
            foreach ($entry as $info) {
                $t[$info['source']] = $info['translation'];
            }

            $file = str_replace('{lang}', $lang, $dst);
            $dir = dirname($file);
            if (false === TMFileHelper::mkdir($dir, 0755)) {
                echo "创建目录{$dir}出错！\n";
                exit(1);
            }

            switch ($mask) {
                case tool_LangEntry::MASK_JS:
                    file_put_contents($file, 'Lang.prototype.pack.' . $lang . '=' . json_encode($t) . ';');
                    break;

                case tool_LangEntry::MASK_PHP:
                    file_put_contents($file, '<?php return ' . var_export($t, true) . ';');
                    file_put_contents($file, php_strip_whitespace($file));
                    break;

                default:
                    echo "未知Mask={$mask}！";
                    exit(1);
                    break;
            }
        }

        echo "完成！\n";
    }

    public function getHelp()
    {
        $help = parent::getHelp();
        $help .= "\nExamples:\n";
        $help .= "php index.php lang export --mask=1 --dst=../webroot/static/data/js/lang/{lang}.js\n";
        $help .= "php index.php lang export --mask=0 --dst=../lang/{lang}/tongji.php\n";

        return $help;
    }
}
