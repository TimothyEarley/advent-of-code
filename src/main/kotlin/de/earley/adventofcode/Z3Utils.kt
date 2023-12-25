package de.earley.adventofcode

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.ArithSort
import com.microsoft.z3.BoolSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.Model
import com.microsoft.z3.RatNum
import com.microsoft.z3.RealSort
import com.microsoft.z3.Sort
import java.math.BigDecimal

context(Context)
operator fun <R : ArithSort> ArithExpr<R>.times(other: Expr<out R>): Expr<R> =
	mkMul(this, other)

context(Context)
operator fun <R : ArithSort> ArithExpr<R>.plus(other: Expr<out R>): Expr<R> =
	mkAdd(this, other)

context(Context)
infix fun <R : Sort> Expr<R>.eq(other: Expr<R>): Expr<BoolSort>? =
	mkEq(this, other)

context(Context)
val Long.real: RatNum
	get() = mkReal(this)

operator fun Model.get(x: Expr<RealSort>): BigDecimal = (getConstInterp(x) as RatNum).let {
	it.bigIntNumerator.toBigDecimal().divide(it.bigIntDenominator.toBigDecimal())
}
