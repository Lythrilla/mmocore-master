package net.Indyuce.mmocore.experience;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExpCurve {
	private final String id;

	/**
	 * Experience needed to level up. Different professions or classes can have
	 * different exp curves so that it is easier to balance.
	 */
	private final List<Long> experience = new ArrayList<>();

	/**
	 * Purely arbitrary but MMOCore needs a default exp curve for everything
	 * otherwise there might be divisions by 0 when trying to update the vanilla
	 * exp bar which requires a 0.0 -> 1.0 float as parameter.
	 *
	 * See {@link PlayerData#refreshVanillaExp()}
	 */
	public static final ExpCurve DEFAULT = new ExpCurve("default", 100, 200, 300, 400, 500);

	/**
	 * Reads an exp curve from a text file, one line after the other. Each exp
	 * value has to be the only thing written on every line
	 *
	 * @param  file        Text file to read data from
	 */
	public ExpCurve(File file) {
        this.id = file.getName().replace(".txt", "").toLowerCase().replace("_", "-").replace(" ", "-");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = reader.readLine()) != null)
                experience.add(Long.valueOf(readLine));
            reader.close();

            Validate.isTrue(!experience.isEmpty(), "There must be at least one exp value in your exp curve");
        } catch(Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

	/**
	 * Public constructor for external plugins
	 *
	 * @param id     Some unique identifier to let other plugin features refer
	 *               to your exp curve.
	 * @param values The exp values, at to be at least one or the constructor
	 *               will throw an error
	 */
	public ExpCurve(String id, long... values) {
		this.id = id;
		for (long value : values)
			experience.add(value);
		Validate.isTrue(!experience.isEmpty(), "There must be at least one exp value in your exp curve");
	}

	public String getId() {
		return id;
	}

	/**
	 * @param  level Level being reached by some player
	 * @return       Experience needed to reach provided level. The level serves
	 *               an index for a list checkup. If the level is higher than
	 *               the list size, it just returns the last value of the list
	 */
	public long getExperience(int level) {
		Validate.isTrue(level > 0, "Level must be stricly positive");
		return experience.get(Math.min(level, experience.size()) - 1);
	}
}
