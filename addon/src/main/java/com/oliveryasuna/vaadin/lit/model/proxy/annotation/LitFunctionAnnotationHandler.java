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

package com.oliveryasuna.vaadin.lit.model.proxy.annotation;

import com.oliveryasuna.vaadin.lit.model.annotation.LitFunction;
import com.oliveryasuna.vaadin.lit.model.proxy.LitModelProxyComponentRegistry;
import com.vaadin.flow.component.page.PendingJavaScriptResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class LitFunctionAnnotationHandler extends BasicLitAnnotationHandler<LitFunction> {

  // Constructors
  //--------------------------------------------------

  protected LitFunctionAnnotationHandler(final Set<Class<? extends Annotation>> requiredAnnotationTypes,
      final Set<Class<? extends Annotation>> mutuallyExclusiveAnnotationTypes) {
    super(LitFunction.class, requiredAnnotationTypes, mutuallyExclusiveAnnotationTypes);
  }

  // Overrides
  //--------------------------------------------------

  // BasicLitAnnotationHandler
  //

  @Override
  protected final Object handle0(final LitFunction annotation, final Object proxy, final Method method, final Object[] arguments) throws Exception {
    final Class<?> returnType = method.getReturnType();

    final PendingJavaScriptResult result = LitModelProxyComponentRegistry.getInstance().get(proxy).getElement()
        .callJsFunction(annotation.name(), arguments);

    if(PendingJavaScriptResult.class.isAssignableFrom(returnType)) return result;

    return null;
  }

}
