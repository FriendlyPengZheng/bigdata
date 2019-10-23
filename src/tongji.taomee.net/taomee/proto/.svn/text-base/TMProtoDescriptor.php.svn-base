<?php
class TMProtoDescriptor
{
    /**
     * @var string Holds the class name of the message
     */
    protected $class;

    /**
     * @var TMProtoField[]
     */
    protected $fields = array();

    /**
     * @var array Cache the relation between names and tags
     */
    protected $names = array();


    /**
     * @param string $class
     */
    public function __construct($class)
    {
        $this->class = trim($class);
    }

    /**
     * @return string
     */
    public function getClass()
    {
        return $this->class;
    }

    /**
     * Obtain the list of fields in the message
     *
     * @return TMProtoField[]
     */
    public function getFields()
    {
        return $this->fields;
    }

    /**
     * Adds a field to the message
     *
     * @param TMProtoField $field
     */
    public function addField(TMProtoField $field)
    {
        $this->fields[$field->number] = $field;
    }

    /**
     * Obtain a field descriptor by its tag number
     *
     * @param  int               $tag
     * @return TMProtoField|null
     */
    public function getField($tag)
    {
        return isset($this->fields[$tag]) ? $this->fields[$tag] : null;
    }

    /**
     * Obtain a field descriptor by its name
     *
     * @param  string            $name
     * @return TMProtoField|null
     */
    public function getFieldByName($name)
    {
        // Check cached map
        if (isset($this->names[$name])) {
            return $this->getField($this->names[$name]);
        }

        // Loop thru all fields to find it
        foreach ($this->fields as $tag => $field) {
            // Cache it for next calls
            $fname = $field->getName();
            $this->names[$fname] = $tag;

            if ($name === $fname) {
                return $field;
            }
        }

        return null;
    }

    /**
     * Check if the given tag number matches a field
     *
     * @param  int  $tag
     * @return bool
     */
    public function hasField($tag)
    {
        return isset($this->fields[$tag]);
    }
}
