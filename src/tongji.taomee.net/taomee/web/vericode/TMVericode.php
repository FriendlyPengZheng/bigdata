<?php
class TMVericode extends TMController
{
     
    /**liujing
    * 是否需要图片的形式
    */
    public $isImage = true;
    
    /**
     * SESSION固定KEY前缀
     */
    const SESSION_VAR_PREFIX = 'TM.vericode';

    /**
     * @brief {string} 固定验证码
     */
    public $fixedVerifyCode;

    /**
     * @brief {integer} 生成验证码最短长度
     */
    public $minLength = 4;

    /**
     * @brief {integer} 生成验证码最长长度
     */
    public $maxLength = 4;

    /**
     * @brief {string} 生成验证码字符范围
     */
    public $letters = '1234567890';

    /**
     * @brief {integer} 验证码图片宽度
     */
    public $width = 90;

    /**
     * @brief {integer} 验证码图片高度
     */
    public $height = 60;

    /**
     * @brief {integer} 验证码图片背景颜色
     */
    public $backColor = 0xFFFFFF;

    /**
     * @brief {boolean} 验证码图片是否透明
     */
    public $transparent = false;

    /**
     * @brief {integer} 验证码图片前景颜色
     */
    public $foreColor = 0x000000;

    /**
     * @brief {array} 验证码图片字体文件路径
     */
    public $fontFile;

    /**
     * @brief {integer} 验证码图片offset
     */
    public $offset;

    /**
     * @brief {integer} 验证码图片padding
     */
    public $padding;

    /**
     * @brief {integer} 验证码图片干扰线条数
     */
    public $lineNumber = 10;

    /**
     * @brief index 
     * 生成验证码图片
     */
    public function index()
    {
        if($this->isImage)
        {
            $this->renderImage($this->getVerifyCode(true));
        }else {
            return $this->getVerifyCode(true);
        }
    }

    /**
     * @brief validate 
     * 验证验证码
     */
    public function validate($code)
    {
        $validate = $code == $this->getVerifyCode();
        $this->getVerifyCode(true);
        return $validate;
    }

    /**
     * @brief getVerifyCode
     * 获取验证码，并放入Session
     *
     * @return {string}
     */
    protected function getVerifyCode($regenerate=false)
    {
        if ($this->fixedVerifyCode !== null) return $this->fixedVerifyCode;

        $session = TM::app()->session;
        $session->open();
        $name = $this->getSessionKey();
        if ($session->get($name) === null || $regenerate) {
            $session->add($name, $this->generateVerifyCode());
            $session->add($name . 'count', 1);
        }
        return $session->get($name);
    }

    /**
     * @brief getSessionKey 
     * 获取唯一的Session Key
     *
     * @return {string}
     */
    protected function getSessionKey()
    {
        return self::SESSION_VAR_PREFIX . TM::app()->getId() . '.' . $this->getId();
    }

    /**
     * @brief generateVerifyCode 
     * 生成随机验证字符
     *
     * @return {string}
     */
    protected function generateVerifyCode()
    {
        if ($this->minLength > $this->maxLength) {
            $this->maxLength = $this->minLength;
        }
        if ($this->minLength < 3) {
            $this->minLength = 3;
        }
        if ($this->maxLength > 20) {
            $this->maxLength = 20; 
        }
        $length = mt_rand($this->minLength, $this->maxLength);

        $letters = $this->letters;
        $letterLength = strlen($letters) - 1;
        $code = ''; 
        for ($i = 0; $i < $length; ++$i) {   
            $code .= $letters[mt_rand(0, $letterLength)];
        }   
        return $code;
    }

    /**
     * @brief renderImage 
     * 生成图片
     *
     * @param {string} $code
     */
    protected function renderImage($code)
    {
        $image = imagecreatetruecolor($this->width, $this->height);

        $backColor = imagecolorallocate($image,
            (int)($this->backColor % 0x1000000 / 0x10000),
            (int)($this->backColor % 0x10000 / 0x100),
            $this->backColor % 0x100
        );
        imagefilledrectangle($image, 0, 0, $this->width, $this->height, $backColor);
        imagecolordeallocate($image, $backColor);

        if ($this->transparent) imagecolortransparent($image, $backColor);

        $foreColor = imagecolorallocate($image,
            (int)($this->foreColor % 0x1000000 / 0x10000),
            (int)($this->foreColor % 0x10000 / 0x100),
            $this->foreColor % 0x100
        );

        if ($this->fontFile === null) {
            $this->fontFile = array(
                dirname(__FILE__) . DS . 'StencilStd.otf',
                dirname(__FILE__) . DS . 'TrajanPro-Bold.otf',
                dirname(__FILE__) . DS . 'VINERITC.TTF'
            );
        } elseif (!is_array($this->fontFile)) {
            $this->fontFile = array($this->fontFile);
        }

        $length = strlen($code);
        $fontFile = $this->fontFile[rand(0, count($this->fontFile) - 1)];

        //加入干扰线
        for ($i = 0; $i < $this->lineNumber; $i++) {   
            $x1 = rand(0, $this->width);
            $y1 = rand(0, $this->height);
            $x2 = rand(0, $this->width);
            $y2 = rand(0, $this->height);

            imageline($image, $x1, $y1, $x2, $y2, imagecolorallocate($image, mt_rand(0, 100), mt_rand(0, 150), mt_rand(0, 200)));
        }
        //加入干扰象素
        for ($i = 0; $i < 120; $i++) {
            imagesetpixel($image, rand() % $this->width, rand()%$this->height , imagecolorallocate($image, mt_rand(100, 255), mt_rand(0, 250), mt_rand(0, 200)));
        }
        $box = imagettfbbox(30, 0, $fontFile, $code);
        $w = $box[4] - $box[0] + $this->offset * ($length - 1); 
        $h = $box[1] - $box[5];
        $scale = min(($this->width - $this->padding * 2) / $w, ($this->height - $this->padding * 2) / $h);
        $x = 10; 
        $y = round($this->height * 27 / 40);
        for($i = 0; $i < $length; ++$i) {
            $fontSize = (int)(rand(26, 32) * $scale * 0.8);
            $angle = rand(-10, 10);
            $letter = $code[$i];
            $box = imagettftext($image, $fontSize, $angle, $x, $y, imagecolorallocate($image, mt_rand(0, 100), mt_rand(0, 150), mt_rand(0, 200)), $fontFile, $letter);
            $x = $box[2] + $this->offset;
        }

        imagecolordeallocate($image, $foreColor);

        header('Pragma: public');
        header('Expires: 0');
        header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
        header('Content-Transfer-Encoding: binary');
        header('Content-Type: image/png');
        imagepng($image);
        imagedestroy($image);
        exit();
    }
}
