<?php
class data_configuration_ConfigurationSet extends TMComponent implements data_configuration_Configurable, Iterator, ArrayAccess, Countable
{
    /**
     * @var integer Index of the current configuration set.
     */
    private static $_counter = 0;

    /**
     * @var data_configuration_Configurable[]
     */
    private $_configurations = array();

    /**
     * @var data_configuration_Configurable Current one.
     */
    private $_configuration = null;

    /**
     * Add one configuration to the set.
     * @param data_configuration_Configurable $configuration
     * @return data_configuration_ConfigurableSet
     */
    public function add(data_configuration_Configurable $configuration)
    {
        $this->_configurations[$configuration->getKey()] = $configuration;
    }

    /**
     * Get key that represents this configuration uniquely.
     * @return string
     */
    public function getKey()
    {
        self::$_counter += 1;
        return self::$_counter;
    }

    /**
     * Whether this set contains configurations.
     * @return boolean
     */
    public function isEmpty()
    {
        return empty($this->_configurations);
    }

    /* Methods of Iterator */

    public function current()
    {
        return current($this->_configurations);
    }

    public function key()
    {
        return key($this->_configurations);
    }

    public function next()
    {
        $this->_configuration = next($this->_configurations);
    }

    public function rewind()
    {
        $this->_configuration = reset($this->_configurations);
    }

    public function valid()
    {
        return $this->_configuration !== false;
    }

    /* Methods of ArrayAccess */

    public function offsetExists($offset)
    {
        return isset($this->_configurations[$offset]);
    }

    public function offsetGet($offset)
    {
        return $this->offsetExists($offset) ? $this->_configurations[$offset] : null;
    }

    public function offsetSet($offset, $value)
    {
        $this->add($value);
    }

    public function offsetUnset($offset)
    {
        if ($this->offsetExists($offset)) {
            unset($this->_configurations[$offset]);
        }
    }

    /* Methods of Countable */

    public function count()
    {
        return count($this->_configurations);
    }

    public function values()
    {
        return array_values($this->_configurations);
    }

    public function __toString()
    {
        return print_r($this->_configurations, true);
    }
}
