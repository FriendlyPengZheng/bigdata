<?php
class gameanalysis_ItemCategoryRel extends TMFormModel
{
    public function tableName()
    {
        return 't_web_item_category_rel';
    }

    public function rules()
    {
        return array(
            array('sstid', 'enum', 'range' => array('_coinsbuyitem_', '_mibiitem_')),
            array('game_id', 'number', 'integerOnly' => true, 'allowEmpty' => false),
            array('item_id', 'exist',
                'className' => 'gameanalysis_Item',
                'condition' => array(
                    'game_id' => $this->game_id,
                    'sstid' => $this->sstid
                )
            )
        );
    }

    public function setItemCategory($categoryIds, $append = false)
    {
        $attrs = array('game_id' => $this->game_id, 'sstid' => $this->sstid, 'item_id' => $this->item_id);
        $relation = $this->findAll(array('condition' => $attrs));
        if (!$append) {
            $this->deleteAllByAttributes($attrs);

            // 检查直接关联道具的顶级类别is_leaf
            $relation = TMArrayHelper::column($relation, null, 'category_id');
            $oCategory = new gameanalysis_ItemCategory();
            foreach ($relation as $categoryId => $useless) {
                $oCategory->checkTypeUnknown($categoryId);
            }
            $relation = array(); // reset it
        } else {
            $relation = TMArrayHelper::column($relation, null, 'category_id');
        }

        foreach ($categoryIds as $categoryId) {
            if (!isset($relation[$categoryId])) {
                $this->category_id = $categoryId;
                $this->setRelation();
            }
        }
    }

    public function setCategoryItem($itemIds, $append = false)
    {
        $attrs = array('game_id' => $this->game_id, 'sstid' => $this->sstid, 'category_id' => $this->category_id);
        $relation = $this->findAll(array('condition' => $attrs));
        if (!$append) {
            $oCategory = new gameanalysis_ItemCategory();
            $oCategory->sstid = $this->sstid;
            $oCategory->game_id = $this->game_id;
            $category = $oCategory->exists($this->category_id);
            $parentId = (int)$category['parent_id'];
            $isLeaf = (int)$category['is_leaf'];
            if ($parentId !== 0) {
                foreach ($relation as $r) {
                    $rel = clone $this;
                    $rel->category_id = $parentId;
                    $rel->item_id = $r['item_id'];
                    $rel->decreaseRefCount();
                }
            }
            $this->deleteAllByAttributes($attrs);
            if ($parentId === 0) {
                $oCategory->checkTypeUnknown($this->category_id);
            }
            $relation = array(); // reset it
        } else {
            $relation = TMArrayHelper::column($relation, null, 'item_id');
        }

        foreach ($itemIds as $itemId) {
            if (!isset($relation[$itemId])) {
                $this->item_id = $itemId;
                $this->setRelation();
            }
        }
    }

    public function setRelation($isParent = false)
    {
        $this->validate($this->attributeNames());

        $oCategory = new gameanalysis_ItemCategory();
        $oCategory->sstid = $this->sstid;
        $oCategory->game_id = $this->game_id;
        $category = $oCategory->exists($this->category_id);
        $parentId = (int)$category['parent_id'];
        $isLeaf = (int)$category['is_leaf'];
        if ($parentId === 0) { // 一级类别
            if ($isLeaf === gameanalysis_ItemCategory::TYPE_UNKNOWN) {
                $oCategory->category_id = $this->category_id;
                $oCategory->is_leaf = gameanalysis_ItemCategory::TYPE_LEAF;
                $oCategory->update(array('is_leaf'));
            }
        } else { // 有父类别时，父类别也映射到道具
            $rel = clone $this;
            $rel->category_id = $parentId;
            $rel->setRelation(true);
        }
        $this->increaseRefCount($isParent);
    }

    public function getRelation()
    {
        $condition = array();
        if (isset($this->sstid)) $condition['sstid'] = $this->sstid;
        if (isset($this->game_id)) $condition['game_id'] = $this->game_id;
        if (isset($this->category_id)) $condition['category_id'] = $this->category_id;
        if (isset($this->item_id)) $condition['item_id'] = $this->item_id;
        return $this->findAll(array('condition' => $condition));
    }

    public function decreaseRefCount()
    {
        $relation = $this->getRelation();
        if (empty($relation)) return;

        $attrs = array('category_id' => $this->category_id, 'sstid' => $this->sstid,
            'game_id' => $this->game_id, 'item_id' => $this->item_id);
        if ($relation[0]['ref_count'] < 2) {
            $this->deleteAllByAttributes($attrs);
            return;
        }
        $this->setRefCount($relation[0]['ref_count'] - 1, $attrs);
    }

    public function increaseRefCount($isParent = false)
    {
        $relation = $this->getRelation();
        $attrs = array('category_id' => $this->category_id, 'sstid' => $this->sstid,
            'game_id' => $this->game_id, 'item_id' => $this->item_id);
        if (empty($relation)) {
            $this->getDb()->createCommand()->insert($this->tableName(), $attrs);
            return;
        }
        if ($isParent) {
            $this->setRefCount($relation[0]['ref_count'] + 1, $attrs);
        }
    }

    protected function setRefCount($refCount, $attrs)
    {
        $this->getDb()->createCommand()->update(
            $this->tableName(),
            // columns
            array('ref_count' => $refCount),
            // condition
            'category_id = :p1 AND sstid = :p2 AND game_id = :p3 AND item_id = :p4',
            // params
            array_combine(array(':p1', ':p2', ':p3', ':p4'), $attrs)
        );
    }

    public function getItemCategory()
    {
        return $this->getDb()->createCommand(
            'SELECT sub.category_id,sub.category_name,sub.parent_id,cat.category_name AS parent_name ' .
            'FROM (' .
                'SELECT cat.category_id,cat.category_name,cat.parent_id ' .
                'FROM t_web_item_category_rel rel ' .
                'INNER JOIN t_web_item_category cat ON rel.category_id=cat.category_id ' .
                'WHERE rel.item_id=? AND rel.sstid=? AND rel.game_id=? AND cat.is_leaf=?' .
            ') sub ' .
            'LEFT JOIN t_web_item_category cat ON sub.parent_id = cat.category_id')
        ->queryAll(array($this->item_id, $this->sstid, $this->game_id, gameanalysis_ItemCategory::TYPE_LEAF));
    }

    public function getCategoryItem()
    {
        return $this->getDb()->createCommand(
            'SELECT sub.item_id,sub.sstid,sub.game_id,item.item_name ' .
            'FROM (' .
                'SELECT rel.item_id,rel.sstid,rel.game_id ' .
                'FROM t_web_item_category_rel rel ' .
                'INNER JOIN t_web_item_category cat ON rel.category_id=cat.category_id ' .
                'WHERE rel.category_id=? AND rel.sstid=? AND rel.game_id=?' .
            ') sub ' .
            'LEFT JOIN t_item_info item ' .
            'ON sub.item_id = item.item_id AND sub.sstid = item.sstid AND sub.game_id = item.game_id')
        ->queryAll(array($this->category_id, $this->sstid, $this->game_id));
    }
}
