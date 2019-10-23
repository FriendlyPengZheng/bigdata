package com.taomee.statlogger.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.taomee.statlogger.StatCommon;

public class StatCommonTest {

	@Test
	public void testKey_no_invalid_chars() {
		assertTrue(StatCommon.key_no_invalid_chars(""));
		assertTrue(StatCommon.key_no_invalid_chars("_"));
		assertTrue(StatCommon.key_no_invalid_chars("__"));
		assertTrue(StatCommon.key_no_invalid_chars("___"));
		
		assertFalse(StatCommon.key_no_invalid_chars(" = "));
		assertFalse(StatCommon.key_no_invalid_chars("_=_"));
		assertFalse(StatCommon.key_no_invalid_chars(" :_"));
		assertFalse(StatCommon.key_no_invalid_chars("o,o"));
		assertFalse(StatCommon.key_no_invalid_chars("_ _"));
		assertFalse(StatCommon.key_no_invalid_chars("_	_"));
		assertFalse(StatCommon.key_no_invalid_chars("__ "));
		assertFalse(StatCommon.key_no_invalid_chars("__."));
		assertFalse(StatCommon.key_no_invalid_chars("__;"));
		assertFalse(StatCommon.key_no_invalid_chars("__|"));
		assertFalse(StatCommon.key_no_invalid_chars("_?_"));
		assertFalse(StatCommon.key_no_invalid_chars("_!_"));
	}

	@Test
	public void testString_firstend_no_invalid_chars() {
		assertTrue(StatCommon.string_firstend_no_invalid_chars("", ""));
		assertTrue(StatCommon.string_firstend_no_invalid_chars(null, ""));
		assertTrue(StatCommon.string_firstend_no_invalid_chars(" ", "_"));
		assertTrue(StatCommon.string_firstend_no_invalid_chars(" ", null));
		
		assertFalse(StatCommon.string_firstend_no_invalid_chars("dsoaijfd_", "_|"));
		assertFalse(StatCommon.string_firstend_no_invalid_chars("|dsoaijfd", "_|"));
		assertFalse(StatCommon.string_firstend_no_invalid_chars("dsoaijfd_", "_|"));
		assertFalse(StatCommon.string_firstend_no_invalid_chars("dsoaijfd|", "_|"));
		assertFalse(StatCommon.string_firstend_no_invalid_chars("_dsoaijfd|", "_|"));
		assertFalse(StatCommon.string_firstend_no_invalid_chars("_dsoaijfd_", "_"));
		assertFalse(StatCommon.string_firstend_no_invalid_chars("_dsoaijfd", "_"));
	}
	
	@Test
	public void testString_no_invalid_chars(){
		assertTrue(StatCommon.string_no_invalid_chars("", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("\\", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("asdf_", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("_asdf_", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("_asdf", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("_as_df_", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("t", "=| \t"));
		assertTrue(StatCommon.string_no_invalid_chars("ttttt", "=| \t"));
		
		assertFalse(StatCommon.string_no_invalid_chars("asdf ", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars(" asdf", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars(" asdf", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("|asdf", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("as|df", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("asdf|", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("|asdf|", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("	asdf", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("as	df", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("asdf	", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("as=df", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("=asdf", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("asdf=", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("tt\ttt", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("tt	tt", "=| \t"));
		assertFalse(StatCommon.string_no_invalid_chars("tt\t	tt", "=| \t"));
	}
}
