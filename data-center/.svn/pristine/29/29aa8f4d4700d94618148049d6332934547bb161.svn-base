<?php
class data_calculator_Calculator extends TMComponent
{
    /**
     * @var array Operands' data.
     */
    private $_operandsData = array();

    /**
     * @var array Expressions to be calculated.
     */
    private $_expressions = array();

    /**
     * @var string Regular expression of operands.
     */
    private $_operandRegex = '/{(\d+)}/';

    /**
     * @var integer The current point.
     */
    private $_cur = 0;

    private $_operandsName = array();

    /**
     * Calculate.
     * @return array|null
     */
    public function calculate()
    {
        if (!$this->_operandsData || !$this->_expressions || !$this->_operandRegex) return;

        $aData = $this->_operandsData;
        $aData['data'] = array();
        foreach ($this->_expressions as $exprInfo) {
            $this->_operandsName = array();
            $aTemp = array('name' => TMArrayHelper::assoc('data_name', $exprInfo, null), 'data' => array());
            TMValidator::ensure(isset($exprInfo['expr']) && $exprInfo['expr'], TM::t('tongji', '表达式不合法！'));
            $iPrecision = TMArrayHelper::assoc('precision', $exprInfo, 2);
            $sUnit = TMArrayHelper::assoc('unit', $exprInfo, '');
            foreach ($this->_operandsData['key'] as $idx => $key) {
                $this->_cur = $idx;
                $sExpr = preg_replace_callback($this->_operandRegex, array($this, '_callback'), $exprInfo['expr']);
                if (strpos($sExpr, 'null') !== false || (strpos($sExpr, '/0') !== false && strpos($sExpr, '/0.') === false)) {
                    $aTemp['data'][$idx] = null;
                } else {
                    $aTemp['data'][$idx] = round(@eval("return (float)$sExpr;"), $iPrecision) . $sUnit;
                }
            }
            if (preg_match('/^{\d+}$/', $exprInfo['expr'])) {
                foreach ($this->_operandsName as $name) {
                    if (($pos = strpos($name, ']')) !== false) {
                        $aTemp['name'] = substr($name, 0, $pos+1).$aTemp['name'];
                    }
                }
            }

            $aData['data'][] = $aTemp;
        }
        return $aData;
    }

    /**
     * Callback for preg_replace_callback.
     * @param array $aMatch
     * @return float
     */
    private function _callback($aMatch)
    {
        if (isset($this->_operandsData['data'][$aMatch[1]])) {
            $this->_operandsName[$aMatch[1]] = $this->_operandsData['data'][$aMatch[1]]['name'];
            if (isset($this->_operandsData['data'][$aMatch[1]]['data'][$this->_cur])) {
                return (float)$this->_operandsData['data'][$aMatch[1]]['data'][$this->_cur];
            }
        }
        return 'null';
    }

    /**
     * Set the operands' data used for calculating.
     * @param array $operandsData
     * @return data_calculator_Calculator
     */
    public function setOperandsData($operandsData)
    {
        $this->_operandsData = $operandsData;
        return $this;
    }

    /**
     * Set expressions to be calculated.
     * @param $expressions
     * @return data_calculator_Calculator
     */
    public function setExpressions($expressions)
    {
        $this->_expressions = $expressions;
        return $this;
    }

    /**
     * Set regular expression of operands.
     * @param string $operandRegex
     * @return data_calculator_Calculator
     */
    public function setOperandRegex($operandRegex)
    {
        $this->_operandRegex = $operandRegex;
        return $this;
    }
}
