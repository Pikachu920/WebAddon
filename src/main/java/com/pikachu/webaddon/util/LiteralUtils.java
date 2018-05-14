package com.pikachu.webaddon.util;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.UnparsedLiteral;

import java.util.stream.Stream;

/**
 * A class that contains methods based around
 * making it easier to deal with {@link UnparsedLiteral}
 * objects.
 */
public class LiteralUtils {

	/**
	 * Checks an {@link Expression} for {@link UnparsedLiteral} objects
	 * and converts them if found.
	 *
	 * @param expr The expression to check for {@link UnparsedLiteral} objects
	 * @param <T>  {@code expr}'s type
	 * @return {@code expr} without {@link UnparsedLiteral} objects
	 */
	@SuppressWarnings("unchecked")
	public static <T> Expression<T> defendExpression(Expression<?> expr) {
		if (expr instanceof ExpressionList) {
			Stream.of(((ExpressionList) expr).getExpressions())
					.forEach(LiteralUtils::defendExpression);
		} else if (expr instanceof UnparsedLiteral) {
			Literal<?> parsedLiteral = ((UnparsedLiteral) expr).getConvertedExpression(Object.class);
			return (Expression<T>) (parsedLiteral == null ? expr : parsedLiteral);
		}
		return (Expression<T>) expr;
	}

	/**
	 * Checks if an Expression contains {@link UnparsedLiteral}
	 * objects.
	 *
	 * @param expr The Expression to check for {@link UnparsedLiteral} objects
	 * @return Whether or not {@code expr} contains {@link UnparsedLiteral} objects
	 */
	public static boolean hasUnparsedLiteral(Expression<?> expr) {
		if (expr instanceof UnparsedLiteral) {
			return true;
		} else if (expr instanceof ExpressionList) {
			return Stream.of(((ExpressionList) expr).getExpressions())
					.anyMatch(e -> e instanceof UnparsedLiteral);
		}
		return false;
	}

	/**
	 * Checks if the passed Expressions are non-null
	 * and do not contain {@link UnparsedLiteral} objects.
	 *
	 * @param expressions The expressions to check for {@link UnparsedLiteral} objects
	 * @return Whether or not the passed expressions contain {@link UnparsedLiteral} objects
	 */
	public static boolean canInitSafely(Expression<?>... expressions) {
		for (int i = 0; i < expressions.length; i++) {
			if (expressions[i] == null || hasUnparsedLiteral(expressions[i])) {
				return false;
			}
		}
		return true;
	}

}
