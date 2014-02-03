/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hawkbrowser.webkit;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.net.ParseException;
import android.util.Log;

public final class URLUtil {

    private static final String LOGTAG = "webkit";

    // to refer to bar.png under your package's asset/foo/ directory, use
    // "file:///android_asset/foo/bar.png".
    static final String ASSET_BASE = "file:///android_asset/";
    // to refer to bar.png under your package's res/drawable/ directory, use
    // "file:///android_res/drawable/bar.png". Use "drawable" to refer to
    // "drawable-hdpi" directory as well.
    static final String RESOURCE_BASE = "file:///android_res/";
    static final String FILE_BASE = "file://";
    static final String PROXY_BASE = "file:///cookieless_proxy/";
    static final String CONTENT_BASE = "content:";

    public static String composeSearchUrl(String inQuery, String template,
                                          String queryPlaceHolder) {
        int placeHolderIndex = template.indexOf(queryPlaceHolder);
        if (placeHolderIndex < 0) {
            return null;
        }

        String query;
        StringBuilder buffer = new StringBuilder();
        buffer.append(template.substring(0, placeHolderIndex));

        try {
            query = java.net.URLEncoder.encode(inQuery, "utf-8");
            buffer.append(query);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }

        buffer.append(template.substring(
                placeHolderIndex + queryPlaceHolder.length()));

        return buffer.toString();
    }

    public static byte[] decode(byte[] url) throws IllegalArgumentException {
        if (url.length == 0) {
            return new byte[0];
        }

        // Create a new byte array with the same length to ensure capacity
        byte[] tempData = new byte[url.length];

        int tempCount = 0;
        for (int i = 0; i < url.length; i++) {
            byte b = url[i];
            if (b == '%') {
                if (url.length - i > 2) {
                    b = (byte) (parseHex(url[i + 1]) * 16
                            + parseHex(url[i + 2]));
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Invalid format");
                }
            }
            tempData[tempCount++] = b;
        }
        byte[] retData = new byte[tempCount];
        System.arraycopy(tempData, 0, retData, 0, tempCount);
        return retData;
    }

    /**
     * @return True iff the url is correctly URL encoded
     */
    static boolean verifyURLEncoding(String url) {
        int count = url.length();
        if (count == 0) {
            return false;
        }

        int index = url.indexOf('%');
        while (index >= 0 && index < count) {
            if (index < count - 2) {
                try {
                    parseHex((byte) url.charAt(++index));
                    parseHex((byte) url.charAt(++index));
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else {
                return false;
            }
            index = url.indexOf('%', index + 1);
        }
        return true;
    }

    private static int parseHex(byte b) {
        if (b >= '0' && b <= '9') return (b - '0');
        if (b >= 'A' && b <= 'F') return (b - 'A' + 10);
        if (b >= 'a' && b <= 'f') return (b - 'a' + 10);

        throw new IllegalArgumentException("Invalid hex char '" + b + "'");
    }

    /**
     * @return True iff the url is an asset file.
     */
    public static boolean isAssetUrl(String url) {
        return (null != url) && url.startsWith(ASSET_BASE);
    }

    /**
     * @return True iff the url is a resource file.
     * @hide
     */
    public static boolean isResourceUrl(String url) {
        return (null != url) && url.startsWith(RESOURCE_BASE);
    }

    /**
     * @return True iff the url is a proxy url to allow cookieless network
     * requests from a file url.
     * @deprecated Cookieless proxy is no longer supported.
     */
    @Deprecated
    public static boolean isCookielessProxyUrl(String url) {
        return (null != url) && url.startsWith(PROXY_BASE);
    }

    /**
     * @return True iff the url is a local file.
     */
    public static boolean isFileUrl(String url) {
        return (null != url) && (url.startsWith(FILE_BASE) &&
                                 !url.startsWith(ASSET_BASE) &&
                                 !url.startsWith(PROXY_BASE));
    }

    /**
     * @return True iff the url is an about: url.
     */
    public static boolean isAboutUrl(String url) {
        return (null != url) && url.startsWith("about:");
    }

    /**
     * @return True iff the url is a data: url.
     */
    public static boolean isDataUrl(String url) {
        return (null != url) && url.startsWith("data:");
    }

    /**
     * @return True iff the url is a javascript: url.
     */
    public static boolean isJavaScriptUrl(String url) {
        return (null != url) && url.startsWith("javascript:");
    }

    /**
     * @return True iff the url is an http: url.
     */
    public static boolean isHttpUrl(String url) {
        return (null != url) &&
               (url.length() > 6) &&
               url.substring(0, 7).equalsIgnoreCase("http://");
    }

    /**
     * @return True iff the url is an https: url.
     */
    public static boolean isHttpsUrl(String url) {
        return (null != url) &&
               (url.length() > 7) &&
               url.substring(0, 8).equalsIgnoreCase("https://");
    }

    /**
     * @return True iff the url is a network url.
     */
    public static boolean isNetworkUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        return isHttpUrl(url) || isHttpsUrl(url);
    }

    /**
     * @return True iff the url is a content: url.
     */
    public static boolean isContentUrl(String url) {
        return (null != url) && url.startsWith(CONTENT_BASE);
    }

    /**
     * @return True iff the url is valid.
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }

        return (isAssetUrl(url) ||
                isResourceUrl(url) ||
                isFileUrl(url) ||
                isAboutUrl(url) ||
                isHttpUrl(url) ||
                isHttpsUrl(url) ||
                isJavaScriptUrl(url) ||
                isContentUrl(url));
    }

    /**
     * Strips the url of the anchor.
     */
    public static String stripAnchor(String url) {
        int anchorIndex = url.indexOf('#');
        if (anchorIndex != -1) {
            return url.substring(0, anchorIndex);
        }
        return url;
    }
    
    /** Regex used to parse content-disposition headers */
    private static final Pattern CONTENT_DISPOSITION_PATTERN =
            Pattern.compile("attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$",
            Pattern.CASE_INSENSITIVE);

    /*
     * Parse the Content-Disposition HTTP Header. The format of the header
     * is defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html
     * This header provides a filename for content that is going to be
     * downloaded to the file system. We only support the attachment type.
     * Note that RFC 2616 specifies the filename value must be double-quoted.
     * Unfortunately some servers do not quote the value so to maintain
     * consistent behaviour with other browsers, we allow unquoted values too.
     */
    static String parseContentDisposition(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                return m.group(2);
            }
        } catch (IllegalStateException ex) {
             // This function is defined as returning null when it can't parse the header
        }
        return null;
    }
}
