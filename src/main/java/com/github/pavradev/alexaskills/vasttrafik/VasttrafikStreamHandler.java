package com.github.pavradev.alexaskills.vasttrafik;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;

public class VasttrafikStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers()
                // Add your skill id below
                //.withSkillId("")
                .build();
    }

    public VasttrafikStreamHandler() {
        super(getSkill());
    }

}
