/*
 * Copyright 2022 Oliver Yasuna
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oliveryasuna.vaadin.lit.model.proxy;

import com.oliveryasuna.commons.language.marker.Immutable;
import com.oliveryasuna.vaadin.lit.exception.LitModelMethodException;
import com.oliveryasuna.vaadin.lit.model.proxy.annotation.LitAnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Javadoc.
@Immutable
public class LitModelInvocationHandler implements InvocationHandler {

  // Static fields
  //--------------------------------------------------

  protected static final String INVALID_LIT_MODEL_METHOD_EXCEPTION_MESSAGE_FORMAT = "Model method [%s] is invalid.";

  // Constructors
  //--------------------------------------------------

  public LitModelInvocationHandler(final Map<Class<? extends Annotation>, LitAnnotationHandler> annotationHandlers) {
    super();

    this.annotationHandlers = Collections.unmodifiableMap(annotationHandlers);
  }

  // Fields
  //--------------------------------------------------

  private final Map<Class<? extends Annotation>, LitAnnotationHandler> annotationHandlers;

  // Overrides
  //--------------------------------------------------

  // InvocationHandler
  //

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final String methodName = method.getName();

    // Required by proxy.
    if(methodName.equals("equals")) return false;
    if(methodName.equals("hashCode")) return 0;

    final Set<Class<? extends Annotation>> methodAnnotationsTypes = Arrays.stream(method.getDeclaredAnnotations())
        .map(Annotation::annotationType)
        .collect(Collectors.toUnmodifiableSet());

    for(final Class<? extends Annotation> methodAnnotationType : methodAnnotationsTypes) {
      if(!annotationHandlers.containsKey(methodAnnotationType)) continue;

      return annotationHandlers.get(methodAnnotationType)
          .handle(proxy, method, args);
    }

    throw new LitModelMethodException(String.format(INVALID_LIT_MODEL_METHOD_EXCEPTION_MESSAGE_FORMAT, methodName));
  }

}
