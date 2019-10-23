<?php
/*
 * TMMailer class.
 */
class TMMailer extends TMComponent
{
    /**
     * @var string Mail transport type (smtp, sendmail or mail), default smtp.
     */
    public $transportType = 'smtp';

    /**
     * @var string The SMTP server host.
     */
    public $host = null;

    /**
     * @var integer The SMTP server port, default 25.
     */
    public $port = 25;

    /**
     * @var string The username to authenticate with.
     */
    public $username = null;

    /**
     * @var string The password to authenticate with.
     */
    public $password = null;

    /**
     * @var string The encryption type (tls or ssl).
     */
    public $encryption = null;

    /**
     * @var string The subject of the message.
     */
    public $subject = null;

    /**
     * @var array The from address of this message.
     * e.g. 'someone@taomee.com' => 'Someone'
     */
    public $from = null;

    /**
     * @var array The To address(es).
     */
    public $to = null;

    /**
     * @var array The Cc address(es).
     */
    public $cc = null;

    /**
     * @var array The Bcc address(es).
     */
    public $bcc = null;

    /**
     * @var Swift_Mailer
     */
    private $_mailer = null;

    /**
     * @var Swift_Message
     */
    private $_message = null;

    /**
     * Init the mailer component.
     */
    public function init()
    {
        require dirname(__FILE__) . '/swift_required.php';
    }

    /**
     * Send the message.
     * @param array $failedRecipients An array of failures
     * @return integer The number of recipients who were accepted for delivery.
     */
    public function send(&$failedRecipients = null)
    {
        return $this->_getMailer()->send($this->_prepare(), $failedRecipients);
    }

    /**
     * Create a message for sending.
     * @return Swift_Message
     */
    public function createMessage()
    {
        $this->_message = Swift_Message::newInstance();
        return $this;
    }

    /**
     * Embed an image from the given data.
     * @param string $data
     * @param string $filename
     * @param string $contentType
     * @return string
     */
    public function embedImage($data, $filename = null, $contentType = null)
    {
        if (isset($this->_message)) {
            return $this->_message->embed(Swift_Image::newInstance($data, $filename, $contentType));
        }
    }

    /**
     * Embed an image from the given path.
     * @param string $path
     * @return string
     */
    public function embedImageFromPath($path)
    {
        if (isset($this->_message)) {
            return $this->_message->embed(Swift_Image::fromPath($path));
        }
    }

    /**
     * Attach an Attachment from the given data.
     * @param string $data
     * @param string $filename
     * @param string $contentType
     * @return TMMailer
     */
    public function attach($data, $filename = null, $contentType = null)
    {
        if (isset($this->_message)) {
            $this->_message->attach(Swift_Attachment::newInstance($data, $filename, $contentType));
        }
        return $this;
    }

    /**
     * Attach an Attachment from the given path.
     * @param string $path
     * @param string $contentType
     * @return TMMailer
     */
    public function attachFromPath($path, $contentType = null)
    {
        if (isset($this->_message)) {
            $this->_message->attach(Swift_Attachment::fromPath($path, $contentType));
        }
        return $this;
    }

    /**
     * Mixin method for message.
     * @param string $method
     * @param args $args
     * @return mixed
     */
    public function __call($method, $args)
    {
        if (in_array($method, $this->_getMessageMixinMethods())) {
            if (isset($this->_message)) {
                $return = call_user_func_array(array(&$this->_message, $method), $args);
                // Allow fluid method calls.
                if ($return instanceof Swift_Message) {
                    return $this;
                } else {
                    return $return;
                }
            }
        }
    }

    /**
     * Prepare for sending the message.
     * @return Swift_Message
     */
    private function _prepare()
    {
        if (!isset($this->_message)) {
            return;
        }

        if (!$this->_message->getSubject()) {
            if (isset($this->subject)) {
                $this->_message->setSubject($this->subject);
            }
        }
        if (!$this->_message->getFrom()) {
            if (isset($this->from)) {
                $this->_message->setFrom($this->from);
            } else {
                throw new TMMailerException(TM::t('taomee', '须指定发送人！'));
            }
        }
        if (!$this->_message->getTo()) {
            if (isset($this->to)) {
                $this->_message->setTo($this->to);
            } else {
                throw new TMMailerException(TM::t('taomee', '须指定接收人！'));
            }
        }
        if (!$this->_message->getCc()) {
            if (isset($this->cc)) {
                $this->_message->setCc($this->cc);
            }
        }
        if (!$this->_message->getBcc()) {
            if (isset($this->bcc)) {
                $this->_message->setBcc($this->bcc);
            }
        }
        return $this->_message;
    }

    /**
     * Get appropriate mailer against the transport type.
     * @return Swift_Mailer
     */
    private function _getMailer()
    {
        if (isset($this->_mailer)) {
            return $this->_mailer;
        }

        $transport = null;
        switch ($this->transportType) {
            default: // default smtp
                if (!isset($this->host) || !isset($this->port)) {
                    throw new TMMailerException(TM::t('taomee', 'SMTP须指定主机名和端口！'));
                }
                $transport = Swift_SmtpTransport::newInstance($this->host, $this->port, $this->encryption);
                if (isset($this->username) && isset($this->password)) {
                    $transport->setUserName($this->username)->setPassword($this->password);
                }
                break;
        }
        return $this->_mailer = Swift_Mailer::newInstance($transport);
    }

    /**
     * Get mixin methods for message.
     * @return array
     */
    private function _getMessageMixinMethods()
    {
        return array(
            'setSubject', 'setBody', 'setDate',
            'setSender', 'setFrom', 'setReplyTo', 'setTo', 'setCc', 'setBcc',
            'setReturnPath', 'setReadReceiptTo', 'setPriority',
            'addTo'
        );
    }
}

/*
 * TMMailerException class.
 */
class TMMailerException extends TMException
{
}
