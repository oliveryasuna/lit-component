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

package com.oliveryasuna.vaadin.lit.component;

import com.googlecode.gentyref.GenericTypeReflector;
import com.oliveryasuna.commons.language.condition.Arguments;
import com.oliveryasuna.vaadin.lit.model.LitModel;
import com.oliveryasuna.vaadin.lit.model.proxy.LitModelInvocationHandler;
import com.oliveryasuna.vaadin.lit.model.proxy.LitModelProxyComponentRegistry;
import com.oliveryasuna.vaadin.lit.model.proxy.annotation.LitAnnotationHandler;
import com.vaadin.flow.component.littemplate.LitTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;

// TODO: Javadoc.
// TODO: Give thanks to Syam Pillai for inspiring something like this. https://github.com/syampillai/SOHelper/blob/master/src/main/java/com/storedobject/helper/LitComponent.java
//       While this is very different, I would not have thought of this without seeing his work.
public abstract class LitComponentBase<M extends LitModel> extends LitTemplate {

  // Constructors
  //--------------------------------------------------

  protected LitComponentBase(final LitModelInvocationHandler modelInvocationHandler) {
    super();

    Arguments.requireNotNull(modelInvocationHandler);

    this.modelInvocationHandler = modelInvocationHandler;
  }

  protected LitComponentBase(final Map<Class<? extends Annotation>, LitAnnotationHandler> annotationHandlers) {
    this(new LitModelInvocationHandler(annotationHandlers));
  }

  // Fields
  //--------------------------------------------------

  private final LitModelInvocationHandler modelInvocationHandler;

  private transient M model;

  // Methods
  //--------------------------------------------------

  @SuppressWarnings("unchecked")
  private M createModelInstance() {
    final Class<? extends M> modelType = getModelType();

    // TODO: Will this work for interfaces that extend LitModel?
    final M model = (M)Proxy.newProxyInstance(modelType.getClassLoader(), new Class[] {modelType}, modelInvocationHandler);

    LitModelProxyComponentRegistry.getInstance().put(model, this);

    return model;
  }

  /**
   * Gets the type of the model.
   * <p>
   * This method can, and should, be overridden.
   *
   * @return The type of the model.
   */
  @SuppressWarnings("unchecked")
  protected Class<? extends M> getModelType() {
    final Type type = GenericTypeReflector.getTypeParameter(getClass().getGenericSuperclass(), LitComponentBase.class.getTypeParameters()[0]);

    if(type instanceof Class || type instanceof ParameterizedType) {
      return (Class<M>)GenericTypeReflector.erase(type);
    }

    if(type == null) throw new IllegalStateException("Raw parameterized type.");

    throw new IllegalStateException("Unsupported parameterized type [" + type.getTypeName() + "].");
  }

  // Getters/setters
  //--------------------------------------------------

  protected final M getModel() {
    return (model != null ? model : (model = createModelInstance()));
  }

}
