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

import com.oliveryasuna.commons.language.marker.Immutable;
import com.oliveryasuna.vaadin.lit.exception.LitModelMethodSignatureException;
import com.oliveryasuna.vaadin.lit.model.annotation.LitProperty;
import com.oliveryasuna.vaadin.lit.model.property.LitPropertyHandlers;
import com.oliveryasuna.vaadin.lit.model.proxy.LitModelProxyComponentRegistry;
import com.vaadin.flow.dom.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

// TODO: Javadoc.
@Immutable
public class LitPropertyAnnotationHandler extends BasicLitAnnotationHandler<LitProperty> {

  // Static fields
  //--------------------------------------------------

  protected static final String REQUIRED_PARAMETER_MISSING_EXCEPTION_MESSAGE_FORMAT = "Annotated [%s] model method [%s] is missing a parameter.";

  protected static final String UNSUPPORTED_RETURN_TYPE_EXCEPTION_MESSAGE_FORMAT = "Annotated [%s] model method [%s] does not support the return type [%s].";

  protected static final String UNSUPPORTED_PARAMETER_TYPE_EXCEPTION_MESSAGE_FORMAT = "" +
      "Annotated [%s] model method [%s] does not support the parameter type [%s].";

  // Constructors
  //--------------------------------------------------

  protected LitPropertyAnnotationHandler(final Map<Class<?>, LitPropertyHandlers> propertyTypeHandlers,
      final Set<Class<? extends Annotation>> requiredAnnotationTypes, final Set<Class<? extends Annotation>> mutuallyExclusiveAnnotationTypes) {
    super(LitProperty.class, requiredAnnotationTypes, mutuallyExclusiveAnnotationTypes);

    this.propertyTypeHandlers = propertyTypeHandlers != null ? Collections.unmodifiableMap(propertyTypeHandlers) : Collections.emptyMap();
  }

  // Fields
  //--------------------------------------------------

  private final Map<Class<?>, LitPropertyHandlers> propertyTypeHandlers;

  // Methods
  //--------------------------------------------------

  protected Object handleGetter(final Class<?> returnType, final Element element, final String propertyName, final LitProperty annotation, final Object proxy,
      final Method method, final Object[] arguments) throws Exception {
    if(!propertyTypeHandlers.containsKey(returnType))
      throw new LitModelMethodSignatureException(String.format(UNSUPPORTED_RETURN_TYPE_EXCEPTION_MESSAGE_FORMAT, annotation.getClass().getSimpleName(),
          method.getName(), returnType.getSimpleName()));

    return propertyTypeHandlers.get(returnType)
        .get(element, propertyName, annotation);
  }

  protected void handleSetter(final Class<?> parameterType, final Element element, final String propertyName, final Object propertyValue,
      final LitProperty annotation, final Object proxy, final Method method, final Object[] arguments) throws Exception {
    if(!propertyTypeHandlers.containsKey(parameterType))
      throw new LitModelMethodSignatureException(String.format(UNSUPPORTED_PARAMETER_TYPE_EXCEPTION_MESSAGE_FORMAT, annotation.getClass().getSimpleName(),
          method.getName(), parameterType.getSimpleName()));

    propertyTypeHandlers.get(parameterType)
        .set(element, propertyName, propertyValue, annotation);
  }

  // Overrides
  //--------------------------------------------------

  // BasicLitAnnotationHandler
  //

  @Override
  protected final Object handle0(final LitProperty annotation, final Object proxy, final Method method, final Object[] arguments) throws Exception {
    final Class<?> returnType = method.getReturnType();

    // TODO: Could abstract out the return types and methods that are called.
    //       This would offer further customization.
    if(returnType.equals(Void.TYPE)) {
      if(method.getParameterCount() < 1)
        throw new LitModelMethodSignatureException(String.format(REQUIRED_PARAMETER_MISSING_EXCEPTION_MESSAGE_FORMAT, annotation.getClass().getSimpleName(),
            method.getName()));

      handleSetter(method.getParameterTypes()[0], LitModelProxyComponentRegistry.getInstance().get(proxy).getElement(), annotation.name(), arguments[0],
          annotation, proxy, method, arguments);

      return null;
    } else {
      return handleGetter(returnType, LitModelProxyComponentRegistry.getInstance().get(proxy).getElement(), annotation.name(), annotation, proxy, method,
          arguments);
    }
  }

  // Getters
  //--------------------------------------------------

  protected final Map<Class<?>, LitPropertyHandlers> getPropertyTypeHandlers() {
    return propertyTypeHandlers;
  }

}
