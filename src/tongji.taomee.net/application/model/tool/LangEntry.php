<?php
class tool_LangEntry extends TMFormModel
{
    /**
     * Category mask constants
     */
    const MASK_PHP = 0;
    const MASK_JS  = 1;

    /**
     * Return the table name.
     *
     * @return string
     */
    public function tableName()
    {
        return 't_web_lang_entry';
    }

    /**
     * Find by category mask
     *
     * @param  int $category
     * @return array
     */
    public function findByCategoryMask($categoryMask)
    {
        $attributes = $this->attributeNames();
        array_unshift($attributes, 'lang');

        return $this->getDb()->createCommand()->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select($attributes)
            ->from($this->tableName())
            ->where('category_slot & ? > 0')
            ->queryAll(array(1 << (int)$categoryMask));
    }
}
