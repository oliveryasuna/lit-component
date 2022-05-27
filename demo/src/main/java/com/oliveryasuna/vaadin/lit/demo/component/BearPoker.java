package com.oliveryasuna.vaadin.lit.demo.component;

import com.oliveryasuna.vaadin.lit.component.LitComponent;
import com.oliveryasuna.vaadin.lit.model.LitModel;
import com.oliveryasuna.vaadin.lit.model.annotation.LitFunction;
import com.oliveryasuna.vaadin.lit.model.annotation.LitProperty;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("x-bear-poker")
@JsModule("src/bear-poker.js") // Compiled into JS (obviously).
public final class BearPoker extends LitComponent<BearPoker.BearPokerModel> {

  // Getters and setters call methods in the model.
  public final String getText() {
    return getModel().getText();
  }

  public final void setText(String text) {
    getModel().setText(text);
  }

  public final int getPokeCount() {
    return getModel().getPokeCount();
  }

  // So do other methods.
  public final void pokeIt() {
    getModel().pokeIt();
  }

  // Here's the fancy-schmancy model declaration.
  // It is implemented through a proxy (a.k.a., magic).
  public interface BearPokerModel extends LitModel {

    @LitProperty(name = "text", defaultValue = "", nullDefaultValue = true)
    String getText();

    @LitProperty(name = "text", defaultValue = "", nullDefaultValue = true)
    void setText(String text);

    @LitProperty(name = "pokeCount", defaultValue = "0")
    int getPokeCount();

    @LitFunction(name = "pokeIt")
    void pokeIt();

  }

}
