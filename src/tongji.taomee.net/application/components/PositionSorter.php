<?php
class PositionSorter extends TMComponent
{
    /**
     * @var array List to be sorted.
     */
    public $list;

    /**
     * @var string Name of primary key for each item.
     */
    public $key;

    /**
     * @var string Name of order.
     */
    public $orderKey = 'display_order';

    /**
     * @var callable Callback function to run for each item in the list.
     *
     * function($key, $order) {
     *     echo "$key -- $order\n";
     * }
     *
     */
    public $callback;


    public function before($current, $after)
    {
        $this->check();

        $this->list[] = [$this->key => 0]; // represent the last position
        $current = (int)$current;
        $after = (int)$after;

        $order = 0;
        $findAfter = false;
        foreach ($this->list as $each) {
            $each[$this->key] = (int)$each[$this->key];
            if ($each[$this->key] === $after) {
                $findAfter = true;
                ++$order;
                call_user_func($this->callback, $current, $order);
            }

            if ($findAfter && $each[$this->key] !== $current) {
                if ($each[$this->key]) {
                    ++$order;
                    call_user_func($this->callback, $each[$this->key], $order);
                }
            } elseif (!$findAfter) {
                $order = $each[$this->orderKey];
            }
        }
    }

    protected function check()
    {
        if (empty($this->list) || !is_array($this->list)) {
            throw new InvalidArgumentException('List must be a non-empty array.');
        }

        if (empty($this->key) || !is_string($this->key)) {
            throw new InvalidArgumentException('Key must be a non-empty string.');
        }

        if (!isset($this->list[0][$this->orderKey])) {
            throw new InvalidArgumentException('List must have the order key.');
        }

        if (!is_callable($this->callback)) {
            throw new InvalidArgumentException('Callback must be callable.');
        }
    }
}
