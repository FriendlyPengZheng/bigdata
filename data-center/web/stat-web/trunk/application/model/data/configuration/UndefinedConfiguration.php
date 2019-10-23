<?php
class data_configuration_UndefinedConfiguration extends data_configuration_Configuration
{
    /**
     * @var integer Index of the current undefined configuration.
     */
    private static $_counter = 0;

    /**
     * Create one data configuration by given parameters.
     */
    public function __construct()
    {
        self::$_counter += 1;

        $this->dataId   = 'undefined' . self::$_counter;
        $this->gpzsId   = 'undefined';
        $this->dataName = 'undefined';
    }
}
