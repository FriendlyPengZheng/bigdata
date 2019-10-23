<?php
class ItemCommand extends TMConsoleCommand
{
    public function actions()
    {
        return array(
            'index'  => true,
            'importCategory' => true
        );
    }

    public function index($args)
    {
        echo "You are here!\n";
    }

    public function importCategory($file, $gameId, $sstid = '_coinsbuyitem_')
    {
        $oDbConnection = TM::app()->getDb();
        $oTransaction = $oDbConnection->beginTransaction();
        $reader = new XmlReader();
        TMValidator::ensure($reader->open($file), TM::t('tongji', '文件不能正常打开！'));
        while ($reader->read()) {
            if ($reader->nodeType !== XMLReader::ELEMENT || $reader->name !== 'item') {
                continue;
            }
            $categoryName = $reader->getAttribute('type');
            $itemId = $reader->getAttribute('id');
            if ($categoryName === null || $itemId === null) {
                continue;
            }
            $oDbConnection->createCommand(
                'INSERT INTO t_web_item_category (category_name,sstid,game_id) VALUES (?,?,?) ' .
                'ON DUPLICATE KEY UPDATE category_id=LAST_INSERT_ID(category_id)'
            )->execute(array($categoryName, $sstid, $gameId));
            $categoryId = $oDbConnection->getLastInsertID();
            $oDbConnection->createCommand(
                'REPLACE INTO t_web_item_category_rel (category_id,sstid,game_id,item_id) VALUES (?,?,?,?)'
            )->execute(array($categoryId, $sstid, $gameId, $itemId));
        }
        $oTransaction->commit();
        echo "Done!\n";
    }
}
