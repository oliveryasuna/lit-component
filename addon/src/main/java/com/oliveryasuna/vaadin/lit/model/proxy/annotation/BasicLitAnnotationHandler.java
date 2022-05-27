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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Javadoc.
@Immutable
public abstract class BasicLitAnnotationHandler<A extends Annotation> implements LitAnnotationHandler {

  // Static fields
  //--------------------------------------------------

  protected static final String REQUIRED_ANNOTATIONS_MISSING_EXCEPTION_MESSAGE_FORMAT = "Annotated [%s] model method [%s] is missing required annotations.";

  protected static final String MUTUALLY_EXCLUSIVE_ANNOTATIONS_PRESENT_EXCEPTION_MESSAGE_FORMAT = "" +
      "Annotation [%s] model method [%s] is also annotated with a prohibited annotation.";

  // Constructors
  //--------------------------------------------------

  protected BasicLitAnnotationHandler(final Class<A> annotationType, final Set<Class<? extends Annotation>> requiredAnnotationTypes,
      final Set<Class<? extends Annotation>> mutuallyExclusiveAnnotationTypes) {
    super();

    this.annotationType = annotationType;

    this.requiredAnnotationTypes = requiredAnnotationTypes != null ? Collections.unmodifiableSet(requiredAnnotationTypes) : Collections.emptySet();
    this.mutuallyExclusiveAnnotationTypes =
        mutuallyExclusiveAnnotationTypes != null ? Collections.unmodifiableSet(mutuallyExclusiveAnnotationTypes) : Collections.emptySet();
  }

  // Fields
  //--------------------------------------------------

  private final Class<A> annotationType;

  private final Set<Class<? extends Annotation>> requiredAnnotationTypes;

  private final Set<Class<? extends Annotation>> mutuallyExclusiveAnnotationTypes;

  // Methods
  //--------------------------------------------------

  protected abstract Object handle0(final A annotation, final Object proxy, final Method method, final Object[] arguments) throws Exception;

  private boolean validateRequiredAnnotations(final Set<Class<? extends Annotation>> annotationTypes) {
    return annotationTypes.equals(requiredAnnotationTypes);
  }

  private boolean validateMutuallyExclusiveAnnotations(final Set<Class<? extends Annotation>> annotationTypes) {
    for(final Class<? extends Annotation> annotationType : annotationTypes) {
      if(mutuallyExclusiveAnnotationTypes.contains(annotationType)) return false;
    }

    return true;
  }

  // Overrides
  //--------------------------------------------------

  // LitAnnotationHandler
  //

  @Override
  public final Object handle(final Object proxy, final Method method, final Object[] arguments) throws Exception {
    final Set<Class<? extends Annotation>> methodAnnotationTypes = Arrays.stream(method.getDeclaredAnnotations())
        .map(Annotation::annotationType)
        .filter(annotationType -> !annotationType.equals(this.annotationType))
        .collect(Collectors.toUnmodifiableSet());

    if(!validateRequiredAnnotations(methodAnnotationTypes))
      throw new LitModelMethodSignatureException(proxy,
          String.format(REQUIRED_ANNOTATIONS_MISSING_EXCEPTION_MESSAGE_FORMAT, annotationType.getSimpleName(), method.getName()));

    if(!validateMutuallyExclusiveAnnotations(methodAnnotationTypes))
      throw new LitModelMethodSignatureException(proxy,
          String.format(MUTUALLY_EXCLUSIVE_ANNOTATIONS_PRESENT_EXCEPTION_MESSAGE_FORMAT, annotationType.getSimpleName(), method.getName()));

    // InvocationHandler ensures that the annotation is present.
    return handle0(method.getAnnotation(annotationType), proxy, method, arguments);
  }

  // Getters
  //--------------------------------------------------

  public final Class<A> getAnnotationType() {
    return annotationType;
  }

  public final Set<Class<? extends Annotation>> getRequiredAnnotationTypes() {
    return requiredAnnotationTypes;
  }

  public final Set<Class<? extends Annotation>> getMutuallyExclusiveAnnotationTypes() {
    return mutuallyExclusiveAnnotationTypes;
  }

}
