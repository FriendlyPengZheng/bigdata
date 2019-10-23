<?php
class TMHttpCookie extends TMComponent
{
    /**
     * @var string Prefix of each cookie name.
     */
    public $prefix = '';

    /**
     * @var array Default options of cookie.
     *
     * path
     *   The path on the server in which the cookie will be available on.
     *   If set to '/', the cookie will be available within the entire domain.
     *   If set to '/foo/', the cookie will only be available within the /foo/ directory
     *   and all sub-directories such as /foo/bar/ of domain.
     *   The default value is the current directory that the cookie is being set in.
     * domain
     *   The domain that the cookie is available to. Setting the domain to 'www.example.com'
     *   will make the cookie available in the www subdomain and higher subdomains.
     *   Cookies available to a lower domain, such as 'example.com'
     *   will be available to higher subdomains, such as 'www.example.com'.
     *   Older browsers still implementing the deprecated RFC 2109 may require a leading . to match all subdomains.
     * secure
     *   Indicates that the cookie should only be transmitted over a secure HTTPS connection from the client.
     *   When set to TRUE, the cookie will only be set if a secure connection exists.
     *   On the server-side, it's on the programmer to send this kind of cookie only on secure connection
     *   (e.g. with respect to $_SERVER['HTTPS']).
     * httponly
     *   When TRUE the cookie will be made accessible only through the HTTP protocol.
     *   This means that the cookie won't be accessible by scripting languages, such as JavaScript.
     *   It has been suggested that this setting can effectively help to reduce identity theft through XSS attacks
     *   (although it is not supported by all browsers), but that claim is often disputed.
     */
    public $defaultOptions = array();

    /**
     * Get cookie value by special name.
     *
     * @param  string $name
     * @param  mixed  $default
     * @return mixed
     */
    public function get($name, $default = null)
    {
        return TMArrayHelper::assoc($this->realname($name), $_COOKIE, $default);
    }

    /**
     * Set a cookie.
     *
     * @param  string     $name
     * @param  string     $value
     * @param  int|string $expire
     * @param  array      $options
     * @return bool
     */
    public function set($name, $value, $expire, $options = array())
    {
        if (!is_numeric($expire)) {
            $expire = strtotime($expire);

            if (false === $expire) {
                throw new TMWebException('The cookie expiration time is not valid.');
            }
        }

        $opts = array_merge($this->defaultOptions, $options);

        return setcookie(
            $this->realname($name),
            $value,
            $expire,
            TMArrayHelper::assoc('path', $opts, '/'),
            TMArrayHelper::assoc('domain', $opts, null),
            TMArrayHelper::assoc('secure', $opts, false),
            TMArrayHelper::assoc('httponly', $opts, false)
        );
    }

    /**
     * Remove from cookie.
     *
     * @param  string $name
     * @return bool
     * @throw  TMWebException If the name is invalid
     */
    public function remove($name, $options = array())
    {
        $opts = array_merge($this->defaultOptions, $options);
        return setcookie(
            $this->realname($name),
            '',
            time() - 3600,
            TMArrayHelper::assoc('path', $opts, '/'),
            TMArrayHelper::assoc('domain', $opts, null),
            TMArrayHelper::assoc('secure', $opts, false),
            TMArrayHelper::assoc('httponly', $opts, false)
        );
    }

    /**
     * Get the real name of cookie.
     *
     * @param  string $name
     * @return string
     * @throw  TMWebException If the name is invalid
     */
    protected function realname($name)
    {
        if (preg_match("/[=,; \t\r\n\013\014]/", $name)) {
            throw new TMWebException(sprintf('The cookie name "%s" contains invalid characters.', $name));
        }

        if (empty($name)) {
            throw new TMWebException(sprintf('The cookie name cannot be empty.', $name));
        }

        return $this->prefix . $name;
    }
}
