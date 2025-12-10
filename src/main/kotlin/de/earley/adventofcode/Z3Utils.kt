package de.earley.adventofcode

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.ArithSort
import com.microsoft.z3.BitVecNum
import com.microsoft.z3.BitVecSort
import com.microsoft.z3.BoolSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.IntNum
import com.microsoft.z3.Model
import com.microsoft.z3.RatNum
import com.microsoft.z3.RealSort
import com.microsoft.z3.Sort
import java.math.BigDecimal

context(ctx: Context)
operator fun <R : ArithSort> ArithExpr<R>.times(other: Expr<out R>): Expr<R> =
	ctx.mkMul(this, other)

context(ctx: Context)
operator fun <R : ArithSort> ArithExpr<R>.plus(other: Expr<out R>): Expr<R> =
	ctx.mkAdd(this, other)

context(ctx: Context)
infix fun Expr<BitVecSort>.xor(other: Expr<BitVecSort>): Expr<BitVecSort> =
	ctx.mkBVXOR(this, other)

context(ctx: Context)
infix fun Expr<BitVecSort>.and(other: Expr<BitVecSort>): Expr<BitVecSort> =
	ctx.mkBVAND(this, other)

context(ctx: Context)
infix fun <R : Sort> Expr<R>.eq(other: Expr<R>): Expr<BoolSort> =
	ctx.mkEq(this, other)

context(ctx: Context)
infix fun <R : ArithSort> Expr<R>.ge(other: Expr<R>): Expr<BoolSort> =
	ctx.mkGe(this, other)


context(ctx: Context)
val Long.real: RatNum
	get() = ctx.mkReal(this)

context(ctx: Context)
val Int.int: IntNum
	get() = ctx.mkInt(this)

context(ctx: Context)
fun Long.bv(size: Int): BitVecNum = ctx.mkBV(this, size)

operator fun Model.get(x: Expr<RealSort>): BigDecimal = (getConstInterp(x) as RatNum).let {
	it.bigIntNumerator.toBigDecimal().divide(it.bigIntDenominator.toBigDecimal())
}