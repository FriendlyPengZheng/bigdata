<?php
class home_Metadata extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_metadata';
    }

    /**
     * Get metadata list by collect_id.
     * @param integer $iCollectId
     * @return array
     */
    public function getListByCollectId($iCollectId)
    {
        return $this->getDb()->createCommand()
            ->select(implode(',', $this->attributeNames()))
            ->from($this->tableName())
            ->where('collect_id = ?')
            ->order('display_order')
            ->queryAll(array($iCollectId));
    }

    /**
     * Delete the metadatas from database by collect_id.
     * @param integer $iCollectId
     * @return boolean
     */
    public function deleteMetadataByCollectId($iCollectId)
    {
        return $this->getDb()->createCommand()->delete(
            $this->tableName(),
            'collect_id = ?',
            array($iCollectId)
        );
    }

    /**
     * Replace a record into the metadata table.
     * @param array $aFields
     * @return boolean
     */
    public function replace($aFields)
    {
        return $this->getDb()->createCommand()->replace($this->tableName(), $aFields);
    }

    public function updateByAttr($aFields, $aAttrs)
    {
        $lines = $params = [];
        foreach ($aAttrs as $name => $value) {
            $lines[] = $this->getDb()->quoteColumnName($name) . '=:' . $name;
            $params[':' . $name] = $value;
        }
        return $this->getDb()->createCommand()->update($this->tableName(), $aFields, implode(' AND ', $lines), $params);
    }
}
