<?php
/**
* @file TMQRCode.php
* @brief 二维码生成
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-12-30
*/
class TMQRCode extends TMComponent
{
    /**
     * @var {string} the cache path for the generated image.
     */
    public $cacheDir;

    /**
     * @var {string} the added image path in the generated image.
     */
    public $embedImagePath;

    /**
     * @var {integer} the quality of the generated image. The value should be 1 to 4.
     */
    public $quality = 2;

    /**
     * @var {integer} the size of the generated image. The value should be 1 to 4.
     */
    public $size = 3;

    /**
     * @var {string} the path which the classes relative to.
     */
    private static $_relativePath = 'system.qrcode.phpqrcode.';

    /*
     * @var {array} the dependents classes or files that this component needs.
     */
    private static $_dependents = array(
        'qrlib'
    );

    /**
     * @see parent
     */
    public function init()
    {
        parent::init();
        TMValidator::ensure(extension_loaded('gd'), TM::t('taomee', 'Extension "gd" must be loaded.'));
        foreach (self::$_dependents as $dependent) {
            require(TM::import(self::$_relativePath . $dependent, false));
        }
        if ($this->cacheDir) {
            TMFileHelper::mkdir($this->cacheDir);
            $this->cacheDir .= DS;
        } else {
            $this->cacheDir = TM::app()->getRuntimePath() . DS;
        }
        if ($this->embedImagePath) {
            if (!file_exists($this->embedImagePath)) {
                throw new TMQRCodeException(TM::t('taomee', '要加入的图片文件不存在！'));
            }
        }
        if (!is_int($this->quality) || $this->quality < 1 || $this->quality > 4) {
            throw new TMQRCodeException(TM::t('taomee', '设置的quality参数不正确！'));
        }
        if (!is_int($this->size) || $this->size < 1 || $this->size > 4) {
            throw new TMQRCodeException(TM::t('taomee', '设置的size参数不正确！'));
        }
    }

    /**
     * @brief png 
     * 生成png类型的二维码
     *
     * @param {string} $message 要写入的文字
     *
     * @return {string} 二维码图片二进制流
     */
    public function png($message)
    {
        $imageFileName = 'test_' . time() . '.png';
        $imageFilePath = $this->cacheDir . $imageFileName;
        QRcode::png($message, $imageFilePath, $this->quality, $this->size);

        if ($this->embedImagePath) {
            $qr   = imagecreatefromstring(file_get_contents($imageFilePath));
            $logo = imagecreatefromstring(file_get_contents($this->embedImagePath));
            $qrWidth  = imagesx($qr); 
            $qrHeight = imagesy($qr); 
            $logoWidth  = imagesx($logo); 
            $logoHeight = imagesy($logo); 
            $logoQrWidth  = $qrWidth / 5; 
            $logoQrHeight = $logoHeight / ($logoWidth / $logoQrWidth); 
            $fromWidth = ($qrWidth - $logoQrWidth) / 2; 
            imagecopyresampled($qr, $logo, $fromWidth, $fromWidth, 0, 0, $logoQrWidth, $logoQrHeight, $logoWidth, $logoHeight); 
            imagepng($qr, $imageFilePath);
        } 
        $handle   = fopen($imageFilePath, 'rb');
        $contents = fread($handle, filesize($imageFilePath));
        fclose($handle);
        unlink($imageFilePath);
        return $contents;
    }
}

/**
 * TMQRCodeException class.
 */
class TMQRcodeException extends TMValidatorException
{
}

