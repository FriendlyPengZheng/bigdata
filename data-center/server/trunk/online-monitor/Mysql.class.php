<?php
require_once("Log.class.php");

class Mysql {
    private $r_mysql = false;
    
    private $s_db_name = "";
    private $s_db_user = "";
    private $s_db_pwd = "";
    private $s_db_ip = "";
    private $log;

    private $r_ret = false;
    private $r_rows = false;

    private $i_affect_cnt = 0;
    private $i_select_cnt = 0;

    public function __construct($s_db_ip, $s_db_user, $s_db_pwd, $s_db_name) {
        $this->s_db_name = $s_db_name;
        $this->s_db_user = $s_db_user;
        $this->s_db_pwd  = $s_db_pwd;
        $this->s_db_ip   = $s_db_ip;

        $this->log = new Log("db_");

        $this->connect();
    }

    public function __destruct() {
        $this->close();
    }

    public function query($s_sql) {
        $this->i_select_cnt = 0;
        $this->i_affect_cnt = 0;
        if($this->r_mysql === false) {
            $this->error();
            return false;
        }

        if(mysql_ping($this->r_mysql) === false ) {
            connect();
            if($this->r_mysql === false) {
                return false;
            }
        }

        if($this->r_ret = mysql_query($s_sql, $this->r_mysql)) {
            $this->i_affect_cnt = mysql_affected_rows($this->r_mysql);
            $this->i_select_cnt = $this->r_ret === true ? 0 : mysql_num_rows($this->r_ret);
            $this->debug("$s_sql [$this->i_affect_cnt:$this->i_select_cnt]");
        } else {
            $this->error("do " . $s_sql);
            return false;
        }
    }

    public function get_selected_cnt() {
        return $this->i_select_cnt;
    }

    public function get_affected_cnt() {
        return $this->i_affect_cnt;
    }

    public function get_next_row() {
        return $this->r_ret === false ? false :mysql_fetch_assoc($this->r_ret);
    }

    private function connect() {
        if($this->r_mysql = mysql_connect($this->s_db_ip, $this->s_db_user, $this->s_db_pwd, true)) {
            $this->debug("connect to " . $this->s_db_ip . " [" . $this->s_db_user . ":" . $this->s_db_pwd  . "]");
            if(mysql_select_db($this->s_db_name, $this->r_mysql)) {
                $this->debug("select " . $this->s_db_name);
		mysql_query("set names utf8", $this->r_mysql);
            } else {
                $this->error("select " . $this->s_db_name);
            }
        } else {
            $this->error("connect to " . $this->s_db_ip . " [" . $this->s_db_user . ":" . $this->s_db_pwd . "]");
        }
    }

    private function close() {
        if($this->r_mysql !== false) {
            if(mysql_close($this->r_mysql)) {
                $this->debug("close connection to mysql");
            }
        }
    }

    private function debug($s_log = "") {
        $this->log->debug_log($s_log);
    }

    private function error($s_log = "") {
        $this->log->error_log($s_log . " error : " . ($this->r_mysql === false ? "not connect" : mysql_error($this->r_mysql)));
    }
    
}

?>
