#!/usr/bin/env node
// ponytail: one-shot generator; re-run after upstream rules.md changes
import { readFileSync, writeFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const root = join(dirname(fileURLToPath(import.meta.url)), '..');
const rulesMd = process.argv[2] || join(root, 'app/src/main/assets/rules/rules.md');
const out = join(root, 'app/src/main/assets/rules/default_rules.json');

const text = readFileSync(rulesMd, 'utf8');
const linkRe = /tarnhelm:\/\/rule\?(\w+)=([^\s)\]]+)/g;
const parameter = [];
const regex = [];
const redirect = [];
const seen = new Set();

for (const [, , encoded] of text.matchAll(linkRe)) {
  const raw = Buffer.from(decodeURIComponent(encoded), 'base64').toString('utf8');
  const key = raw;
  if (seen.has(key)) continue;
  seen.add(key);
  const obj = JSON.parse(raw);
  if ('f' in obj) parameter.push(obj);
  else if ('b' in obj) regex.push(obj);
  else redirect.push(obj);
}

writeFileSync(out, JSON.stringify({ parameter, regex, redirect }, null, 2) + '\n');
console.log(`Wrote ${parameter.length} parameter, ${regex.length} regex, ${redirect.length} redirect rules -> ${out}`);
