<?php
interface TMDisplayInterface
{
    /**
     * @brief assignListener 
     * 监听display
     *
     * @param {object} $controller
     */
    function assignListener(TMController $controller);
}
