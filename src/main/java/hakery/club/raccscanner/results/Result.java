package hakery.club.raccscanner.results;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.util.Obfuscator;
import hakery.club.raccscanner.results.util.result.ObfuscatorResult;
import hakery.club.raccscanner.results.util.result.impl.AllatoriResult;
import hakery.club.raccscanner.results.util.result.impl.DashOResult;
import hakery.club.raccscanner.results.util.result.impl.ParamorphismResult;
import hakery.club.raccscanner.results.util.result.impl.StringerResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Result {

    ArrayList<ObfuscatorResult> results;
    Raccoon parent;

    public Result(Raccoon raccoon) {
        this.results = new ArrayList<>();
        this.parent = raccoon;

        this.results.add(new ParamorphismResult(this.parent));
        this.results.add(new StringerResult(this.parent));
        this.results.add(new AllatoriResult(this.parent));
        this.results.add(new DashOResult(this.parent));

        /**
         * Don't forget to parse
         */
        this.results.forEach(obfuscatorResult -> obfuscatorResult.parse());
    }

    public <T> T getObfuscatorResult(Obfuscator obfuscator) {
        Optional<ObfuscatorResult> resultOptional = this.results.stream().filter(obfuscatorResult -> obfuscatorResult.getObfuscator() == obfuscator)
                .findAny();

        if (resultOptional.isPresent())
            return (T) resultOptional.get();

        try {
            throw new Exception(String.format("Obfuscator %s result's weren't initialized!", obfuscator.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void printResults() {
        this.results.forEach(obfuscatorResult -> this.parent.getLogger().log("Scanner result for %s = [%d%%]", obfuscatorResult.getObfuscator().toString(), obfuscatorResult.getPercentage()));
    }

    public List<String> getResults() {
        List<String> toReturn = new ArrayList<>();
        this.results.forEach(obfuscatorResult -> toReturn.add(String.format("Scanner result for %s = [%d%%]", obfuscatorResult.getObfuscator().toString(), obfuscatorResult.getPercentage())));
        return toReturn;
    }

}
