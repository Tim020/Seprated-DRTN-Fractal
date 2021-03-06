/**
 * SEPR project inherited from DRTN.
 * Any changes are marked by preceding comments.
 * 
 * Executables availabe at: https://seprated.github.io/Assessment4/Executables.zip
**/
package io.github.teamfractal.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.screens.GameScreen;
import io.github.teamfractal.screens.Overlay;
import io.github.teamfractal.util.ResourceGroupFloat;
import io.github.teamfractal.util.ResourceGroupInteger;

public class PlotEffect extends Array<ResourceGroupFloat> {

    /**
     * The name of the effect
     */
    private String name;

    /**
     * A description of the effect
     */
    private String description;

    /**
     * Object containing a method that the effect can automatically trigger if and when it is run
     */
    private Runnable runnable;

    /**
     * Array storing all of the plots to have been affected by this effect (in the order by which they were affected)
     */
    private Array<LandPlot> plotRegister;

    /**
     * Overlay to provide a visual indication of the effect's presence and influences
     */
    private Overlay overlay;

    /**
     * Constructor that imports the parameters of the effect along with a custom block of code in which it can be used
     *
     * @param name        The name of the effect
     * @param description A description of the effect
     * @param modifiers   The production modifiers that the effect can impose
     * @param runnable    The code to be executed when the effect is imposed through natural means
     */
    public PlotEffect(String name, String description, ResourceGroupFloat modifiers, Runnable runnable) {
        this.name = name;
        this.description = description;
        //Stores the effect's name and description for future reference

        super.add(modifiers);
        //Store the effect's modifiers at the base of the internal stack

        this.runnable = runnable;
        //Assign the effect to the proprietary method provided

        this.plotRegister = new Array<LandPlot>();
        //Establish the separate LandPlot stack to track affected tiles

        this.overlay = new Overlay(Color.GOLDENROD, Color.WHITE, 3);
        //Construct a visual interface through which the effect can be identified
    }

    /**
     * Overloaded constructor that imports the parameters of the effect and sets it up to be applied to a specific
     * plot in a specific way upon usage
     *
     * @param name        The name of the effect
     * @param description A description of the effect
     * @param modifiers   The production modifiers that the effect can impose
     * @param plot        The plot which the effect is to be applied to
     */
    public PlotEffect(String name, String description, ResourceGroupFloat modifiers, final LandPlot plot, final int mode) {
        this(name, description, modifiers, new Runnable() {
            @Override
            public void run() {
                /*
                Intentionally empty.
                */
            }
        });

        this.runnable = new Runnable() {
            @Override
            public void run() {
                impose(plot, mode);
            }
        };
    }

    /**
     * Method that populates the effect's associated overlay
     */
    public void constructOverlay(final GameScreen gameScreen) {
        TextButton.TextButtonStyle overlayButtonStyle = new TextButton.TextButtonStyle();
        overlayButtonStyle.font = gameScreen.getGame().headerFontRegular.font();
        overlayButtonStyle.pressedOffsetX = -1;
        overlayButtonStyle.pressedOffsetY = -1;
        overlayButtonStyle.fontColor = Color.WHITE;
        //Set the visual parameters for the [CLOSE] button on the overlay

        Label headerLabel = new Label("PLOT EFFECT IMPOSED", new Label.LabelStyle(gameScreen.getGame().headerFontRegular.font(), Color.YELLOW));
        Label titleLabel = new Label(name, new Label.LabelStyle(gameScreen.getGame().headerFontLight.font(), Color.WHITE));
        Label descriptionLabel = new Label(description, new Label.LabelStyle(gameScreen.getGame().smallFontLight.font(), Color.WHITE));
        //Construct labels to state the type, name and description of this effect

        headerLabel.setAlignment(Align.left);
        titleLabel.setAlignment(Align.right);
        descriptionLabel.setAlignment(Align.left);
        //Align the aforementioned labels against the edges of the overlay's internal table...

        overlay.table().add(headerLabel).width(300).left();
        overlay.table().add(titleLabel).width(descriptionLabel.getWidth() - 300).right();
        overlay.table().row();
        overlay.table().add(descriptionLabel).left().colspan(2).padTop(5).padBottom(20);
        //...and then add them to it

        overlay.table().row().colspan(2);
        TextButton closeButton = new TextButton("CLOSE", overlayButtonStyle);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.removeOverlay();
            }
        });

        overlay.table().add(closeButton);
        //Set up and add a [CLOSE] button to the overlay

        overlay.resize(descriptionLabel.getWidth() + 20, headerLabel.getHeight() + descriptionLabel.getHeight() + closeButton.getHeight() + 35);
        //Resize the overlay to fit around the sizes of the labels that were added to it
    }

    /**
     * Imposes the effect's modifiers on the provided plot
     * Assumes that the modifiers to be imposed at any given time will be at the head of the internal stack
     *
     * @param plot The plot to be affected
     * @param mode The mode of effect [0: ADD | 1: MULTIPLY | 2: OVERWRITE]
     */
    public void impose(LandPlot plot, int mode) {
        //Declare temporary arrays to handle modifier modifications
        ResourceGroupFloat originalModifiers = new ResourceGroupFloat();
        ResourceGroupFloat newModifiers;

        //Assume that the modifiers on the top of the stack are the modifiers to be imposed
        newModifiers = super.pop();
        originalModifiers.setResource(ResourceType.FOOD, plot.productionModifiers.getFood().floatValue());
        originalModifiers.setResource(ResourceType.ENERGY, plot.productionModifiers.getEnergy().floatValue());
        originalModifiers.setResource(ResourceType.ORE, plot.productionModifiers.getOre().floatValue());


        switch (mode) {
            case (0):
                //MODE 0: Add/subtract to/from the original modifiers
                plot.productionModifiers.setResource(ResourceType.FOOD, plot.productionModifiers.getFood().intValue() + newModifiers.getFood().intValue());
                plot.productionModifiers.setResource(ResourceType.ENERGY, plot.productionModifiers.getEnergy().intValue() + newModifiers.getEnergy().intValue());
                plot.productionModifiers.setResource(ResourceType.ORE, plot.productionModifiers.getOre().intValue() + newModifiers.getOre().intValue());
                break;
            case (1):
                //MODE 1: Multiply the original modifier
                plot.productionModifiers.setResource(ResourceType.FOOD, new Float(plot.productionModifiers.getFood() * newModifiers.getFood()).intValue());
                plot.productionModifiers.setResource(ResourceType.ENERGY, new Float(plot.productionModifiers.getEnergy() * newModifiers.getEnergy()).intValue());
                plot.productionModifiers.setResource(ResourceType.ORE, new Float(plot.productionModifiers.getOre() * newModifiers.getOre()).intValue());
                break;
            case (2):
                //MODE 2: Replace the original modifiers
                plot.productionModifiers.setResource(ResourceType.FOOD, newModifiers.getFood().intValue());
                plot.productionModifiers.setResource(ResourceType.ENERGY, newModifiers.getEnergy().intValue());
                plot.productionModifiers.setResource(ResourceType.ORE, newModifiers.getOre().intValue());
                break;
        }

        //Add the tile's original modifiers to the stack for later access...
        super.add(originalModifiers);

        //...and return the imposed modifiers to the top of the stack
        super.add(newModifiers);

        //Push the plot that was modified on to the appropriate registration stack
        plotRegister.add(plot);
    }

    /**
     * Reverts the changes made by the effect to the last plot that it was assigned to
     */
    private void revert() {
        if (plotRegister.size > 0) {
            ResourceGroupFloat originalModifiers;
            LandPlot lastPlot;

            swapTop();
            originalModifiers = super.pop();
            //Swap the first two modifier arrays at the head of the stack to access the array that was originally
            //bound to the last affected plot

            lastPlot = plotRegister.pop();
            //Retrieve the last plot that this effect was imposed upon

            lastPlot.productionModifiers.setResource(ResourceType.FOOD, originalModifiers.getFood().intValue());
            lastPlot.productionModifiers.setResource(ResourceType.ENERGY, originalModifiers.getEnergy().intValue());
            lastPlot.productionModifiers.setResource(ResourceType.ORE, originalModifiers.getOre().intValue());

            //Restore the original production modifiers of the aforementioned plot
        }
    }

    /**
     * Reverts all affected tiles back to their original states
     */
    public void revertAll() {
        while (plotRegister.size > 0) {
            revert();
        }
    }

    /**
     * Swaps the positions of the first two values within the internal stack
     */
    private void swapTop() {
        if (super.size > 1) {
            ResourceGroupFloat i = super.pop();
            ResourceGroupFloat j = super.pop();
            super.add(i);
            super.add(j);
        }
    }

    /**
     * Executes the runnable
     */
    public void executeRunnable() {
        runnable.run();
    }

    /**
     * Getter for the overlay
     *
     * @return The overlay of the effect
     */
    public Overlay overlay() {
        return overlay;
    }
}
