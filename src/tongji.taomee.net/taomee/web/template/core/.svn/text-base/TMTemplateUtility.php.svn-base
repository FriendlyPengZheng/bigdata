<?php
/**
 * Template Utility
 */
class TMTemplateUtility
{
    /**
     * Writes file in a safe way to disk
     *
     * @param  string     $filepath complete filepath
     * @param  string     $contents file content
     * @param  TMTemplate $template template instance
     * @return boolean true
     */
    public static function writeFile($filepath, $contents, TMTemplate $template)
    {
        $errorReporting = error_reporting();
        // 关闭提示和警告
        error_reporting($errorReporting & ~E_NOTICE & ~E_WARNING);

        if ($template->filePerms !== null) {
            $oldUmask = umask(0);
        }

        $dirpath = dirname($filepath);
        if ($dirpath !== '.' && !file_exists($dirpath)) {
            mkdir($dirpath, $template->dirPerms === null ? 0777 : $template->dirPerms, true);
        }

        // 首先生成一个临时文件，然后再重命名
        $tmpFile = $dirpath . '/' . uniqid('wrt', true);
        if (!file_put_contents($tmpFile, $contents)) {
            error_reporting($errorReporting);
            throw new TMTemplateException("Unable to write file {$tmpFile}");
            return false;
        }

        $success = @rename($tmpFile, $filepath);
        if (!$success) {
            @unlink($filepath);
            $success = @rename($tmpFile, $filepath);
        }
        if (!$success) {
            error_reporting($errorReporting);
            throw new TMTemplateException("Unable to write file {$filepath}");
            return false;
        }

        if ($template->filePerms !== null) {
            chmod($filepath, $template->filePerms);
            umask($oldUmask);
        }
        error_reporting($errorReporting);

        return true;
    }
}
