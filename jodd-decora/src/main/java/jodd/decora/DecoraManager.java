// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.decora;

import jodd.io.FileUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Decora manager defines if some request should be decorated and resolves decorators paths.
 */
public class DecoraManager {

	private static DecoraManager DECORA_MANAGER;

	/**
	 * Returns Decora manager.
	 */
	public static DecoraManager get() {
		return Objects.requireNonNull(DECORA_MANAGER, "Only one instance of DecoraManager should be used.");
	}

	public static final String DEFAULT_DECORATOR = "/decora/main.jsp";

	public DecoraManager() {
		DECORA_MANAGER = this;
	}

	// ---------------------------------------------------------------- properties

	protected boolean decorateErrorPages;

	public boolean isDecorateErrorPages() {
		return decorateErrorPages;
	}

	public void setDecorateErrorPages(final boolean decorateErrorPages) {
		this.decorateErrorPages = decorateErrorPages;
	}

	// ---------------------------------------------------------------- cache

	protected Map<String, char[]> contentMap;
	protected Map<String, File> filesMap;

	public DecoraManager registerDecorator(final String path, final char[] content) {
		if (contentMap == null) {
			contentMap = new HashMap<>();
		}
		contentMap.put(path, content);
		return this;
	}
	public DecoraManager registerDecorator(final String path, final File decorator) {
		if (filesMap == null) {
			filesMap = new HashMap<>();
		}
		filesMap.put(path, decorator);
		return this;
	}

	/**
	 * Lookups the decorator for given decorator path.
	 * Returns {@code null} if decorator is not registered, indicating that content should be
	 * read using the dispatcher.
	 */
	public char[] lookupDecoratorContent(final String path) {
		if (contentMap != null) {
			final char[] data = contentMap.get(path);
			if (data != null) {
				return data;
			}
			final File file = filesMap.get(path);
			if (file != null) {
				try {
					return FileUtil.readChars(file);
				} catch (IOException e) {
					throw new DecoraException("Unable to read Decrator files", e);
				}
			}
		}
		return null;
	}

	// ---------------------------------------------------------------- check

	/**
	 * Determines if a request should be decorated.
	 * By default returns <code>true</code>.
	 */
	public boolean decorateRequest(final HttpServletRequest request) {
		return true;
	}

	/**
	 * Determines if some content type should be decorated.
	 * By default returns <code>true</code>.
	 */
	public boolean decorateContentType(final String contentType, final String mimeType, final String encoding) {
		return true;
	}

	/**
	 * Determines if buffering should be used for some HTTP status code.
	 * By default returns <code>true</code> for status code 200 and, optionally,
	 * for error pages (status code {@literal >=} 400).
	 */
	public boolean decorateStatusCode(final int statusCode) {
		return (statusCode == 200) || (decorateErrorPages && statusCode >= 400);
	}

	// ---------------------------------------------------------------- find

	/**
	 * Resolves decorator path based on request and action path.
	 * If decorator is not found, returns <code>null</code>.
	 * By default applies decorator on all *.html pages.
	 */
	public String resolveDecorator(final HttpServletRequest request, final String actionPath) {
		if (actionPath.endsWith(".html")) {
			return DEFAULT_DECORATOR;
		}
		return null;
	}

}
