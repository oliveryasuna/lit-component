# Lit Component.
 
Model [LitElement](https://lit.dev/) web components with Vaadin.

## Getting Started

`bear-poker.ts`:

```typescript
@customElement('x-bear-poker')
export class BearPokerElement extends LitElement {

  @property()
  private text: String = 'You see a huge grizzly bear...';
  
  @property()
  private pokeCount: number = 0;

  protected render(): TemplateResult {
    return html`
      ${this.text}
    `;
  }
  
  public pokeIt(): void {
    this.pokeCount++;
    
    if(this.pokeCount === 1) {
      this.text = 'Why would you poke a bear?';
    } else if(this.pokeCount === 2) {
      this.text = 'You really are dumb, aren\'t you?';
    } else if(this.pokeCount === 3) {
      this.text = 'You\'re dead now. Happy?';
    } else {
      this.text = 'You can\'t poke the bear from the grave!';
    }
  }

}
```

`BearPoker.java`:

```java
@Tag("x-bear-poker")
@JsModule("src/bear-poker.js") // Compiled into JS (obviously).
public class BearPoker extends LitComponent<BearPoker.BearPokerModel> {

  // Getters and setters call methods in the model.
  public String getText() { return getModel().getText(); }
  public void setText(String text) { getModel().setText(text); }
  
  public int getPokeCount() { return getModel().getPokeCount(); }
  
  // So do other methods.
  public void pokeIt() { getModel().pokeIt(); }

  // Here's the fancy-schmancy model declaration.
  // It is implemented through a proxy (a.k.a., magic).
  interface BearPokerModel extends LitModel {

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
```

## How Does It Work?

One teensy scoop of <span style="padding: 4px; background-color: hsl(359, 60%, 59%);"><span style="color: hsl(0, 62.5%, 85%);">L</span><span style="color: hsl(0, 75%, 85%);">O</span><span style="color: hsl(0, 87.5%, 85%);">V</span><span style="color: hsl(0, 100%, 85%);">E</span></span> a lot of <span style="padding: 4px; background-color: skyblue;"><span style="color: red;">M</span><span style="color: orange;">A</span><span style="color: yellow;">G</span><span style="color: green;">I</span><span style="color: blue;">C</span><span style="color: purple;">!</span></span> topped with <span style="padding: 4px; color: hsl(190, 10%, 35%); background-color: hsl(190, 100%, 50%);">DREAMS</span>.

[//]: # (TODO: Real info.)

## License

This code is under the [BSD 3-Clause](LICENSE.txt).

## Sponsoring

If you like my work and want to support it, please consider [sponsoring](https://github.com/sponsors/oliveryasuna) me. It's how I make the time to code great things!
