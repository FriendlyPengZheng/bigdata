<?php
class gameanalysis_ItemCategory extends TMFormModel
{
    const TYPE_UNKNOWN = 2;
    const TYPE_LEAF = 1;
    const TYPE_NONLEAF = 0;

    public function tableName()
    {
        return 't_web_item_category';
    }

    public function rules()
    {
        return array(
            array('category_name', 'string', 'max' => 255, 'min' => 1),
            array('sstid', 'enum', 'range' => array('_coinsbuyitem_', '_mibiitem_')),
            array('game_id', 'number', 'integerOnly' => true, 'allowEmpty' => false),
            array('parent_id', 'checkParent'),
            array('category_name', 'unique',
                'condition' => array(
                    'sstid' => $this->sstid,
                    'game_id' => $this->game_id,
                    'parent_id' => $this->parent_id
                ),
                'exclude' => array(
                    'category_id' => $this->category_id
                )
            )
        );
    }

    public function checkParent($parentId, $attr, $params)
    {
        $parentId = (int)$parentId;
        if ($parentId === 0) return true;

        if (($parent = $this->exists($parentId, false)) === false) return false;
        if ((int)$parent['is_leaf'] === self::TYPE_LEAF) return false;

        return true;
    }

    public function beforeInsert()
    {
        $this->parent_id = (int)$this->parent_id;
        // 未知
        if ($this->parent_id === 0) {
            $this->is_leaf = self::TYPE_UNKNOWN;
        // 将父类别更新为非叶子
        } else {
            $parent = clone $this;
            $parent->category_id = $this->parent_id;
            $parent->is_leaf = self::TYPE_NONLEAF;
            $parent->update(array('is_leaf'));
        }
        return true;
    }

    public function beforeDelete()
    {
        if (($category = $this->exists($this->category_id, false)) === false) return false;
        $parentId = (int)$category['parent_id'];
        $isLeaf = (int)$category['is_leaf'];

        $rel = new gameanalysis_ItemCategoryRel();
        $rel->sstid = $this->sstid;
        $rel->game_id = $this->game_id;

        // 叶子
        if ($parentId !== 0) {
            // 如果类别有关联道具，删除时要将其父的道具关联一起删除
            if ($isLeaf === self::TYPE_LEAF) {
                $relation = $rel->findAll(array('condition' => array('category_id' => $this->category_id)));
                foreach ($relation as $r) {
                    $rel->category_id = $parentId;
                    $rel->item_id = $r['item_id'];
                    $rel->decreaseRefCount();
                }
            }
            $this->checkTypeUnknown($parentId, $this->category_id);
        // 非叶子删除时同时删除其子类别
        } else {
            $children = $this->findAll(array('condition' => array('parent_id' => $this->category_id)));
            foreach ($children as $info) {
                $child = clone $this;
                $child->category_id = $info['category_id'];
                $child->delete();
            }
        }

        $rel->deleteAllByAttributes(array('category_id' => $this->category_id));
        return true;
    }

    public function move()
    {
        $category = $this->exists($this->category_id);
        TMValidator::ensure((int)$category['is_leaf'] !== self::TYPE_NONLEAF, TM::t('tongji', '移动类别是顶级！'));
        $exParentId = (int)$category['parent_id'];
        $this->parent_id = (int)$this->parent_id;
        if ($exParentId === $this->parent_id) return;

        $oRel = new gameanalysis_ItemCategoryRel();
        $oRel->sstid = $this->sstid;
        $oRel->game_id = $this->game_id;
        $oRel->category_id = $this->category_id;
        $oRel->item_id = null;
        $relations = $oRel->getRelation();
        if ($exParentId !== 0) { // 解除父对子item的映射
            foreach ($relations as $relation) {
                $oRel->category_id = $exParentId;
                $oRel->item_id = $relation['item_id'];
                $oRel->decreaseRefCount();
            }
            $this->checkTypeUnknown($exParentId, $this->category_id);
        }

        if ($this->parent_id !== 0) {
            $parent = $this->exists($this->parent_id);
            $isLeaf = (int)$parent['is_leaf'];
            TMValidator::ensure($isLeaf !== self::TYPE_LEAF, TM::t('tongji', '移向类别是底级！'));
            if ($isLeaf === self::TYPE_UNKNOWN) {
                $oCategory = clone $this;
                $oCategory->category_id = $this->parent_id;
                $oCategory->is_leaf = self::TYPE_NONLEAF;
                $oCategory->update(array('is_leaf'));
            }
            foreach ($relations as $relation) {
                $oRel->category_id = $this->parent_id;
                $oRel->item_id = $relation['item_id'];
                $oRel->increaseRefCount(true);
            }
        }

        $this->update(array('parent_id'));
    }

    public function checkTypeUnknown($categoryId, $exclude = array())
    {
        $category = $this->findAll(array('condition' => array('category_id' => $categoryId)));
        if (empty($category)) return;

        if ((int)$category[0]['parent_id'] !== 0) return;
        switch ((int)$category[0]['is_leaf']) {
            case self::TYPE_UNKNOWN:
                return;
                break;

            case self::TYPE_LEAF:
                $oRel = new gameanalysis_ItemCategoryRel();
                $oRel->category_id = $categoryId;
                $relation = $oRel->getRelation();
                if (!empty($relation)) return;
                break;

            case self::TYPE_NONLEAF:
                $exclude = (array)$exclude;
                $children = $this->findAll(array('condition' => array('parent_id' => $categoryId)));
                foreach ($children as $idx => $child) {
                    if (in_array($child['category_id'], $exclude)) {
                        unset($children[$idx]);
                    }
                }
                if (!empty($children)) return;
                break;
        }

        $oCategory = clone $this;
        $oCategory->category_id = $categoryId;
        $oCategory->is_leaf = self::TYPE_UNKNOWN;
        $oCategory->update(array('is_leaf'));
    }

    public function exists($categoryId, $sure = true)
    {
        $category = $this->findAll(array('condition' => array(
            'sstid' => $this->sstid,
            'game_id' => $this->game_id,
            'category_id' => $categoryId
        )));

        if ($category) {
            return $category[0];
        } elseif ($sure) {
            TMValidator::ensure(false, TM::t('tongji', '类别不存在！'));
        } else {
            return false;
        }
    }
}
