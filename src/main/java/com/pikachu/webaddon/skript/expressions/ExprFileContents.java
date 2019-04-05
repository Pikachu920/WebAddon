package com.pikachu.webaddon.skript.expressions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("File Contents")
@Description("The contents of a file")
@Examples("send back file contents of \"plugins/Skript/scripts/website/index.html\"")
public class ExprFileContents extends SimplePropertyExpression<String, String> {

	static {
		PropertyExpression.register(ExprFileContents.class, String.class,
				"file contents", "strings");
	}

	@Override
	public String convert(String path) {
		try {
			return Files.lines(Paths.get(path), StandardCharsets.UTF_8)
					.collect(Collectors.joining("\n"));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	protected String getPropertyName() {
		return "file contents";
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
