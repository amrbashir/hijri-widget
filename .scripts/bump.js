const path = require("path");
const fs = require("fs");

const bump = process.argv[2];

if (!["major", "minor", "patch"].includes(bump)) {
    console.error(`Unkown bump type: ${bump}`);
    process.exit(1);
}

const appGradle = path.resolve(__dirname, "..", "app", "build.gradle.kts");

const content = fs.readFileSync(appGradle, "utf8");

const versionCodeRe = /versionCode = ([0-9]*)/;
const versionNameRe = /versionName = "([0-9]*\.[0-9]*\.[0-9])"/;

const [_, versionCode] = versionCodeRe.exec(content);
const versionCodeNum = Number(versionCode);
const newVersionCode = versionCodeNum + 1;

const [_a, versionName] = versionNameRe.exec(content);
const [mj, mi, pa] = versionName.split(".");

let major = Number(mj);
let minor = Number(mi);
let patch = Number(pa);

switch (bump) {
    case "major":
        major += 1;
        minor = 0;
        patch = 0;
        break;
    case "minor":
        minor += 1;
        patch = 0;
        break;
    case "patch":
        patch = +1;
        break;
    default:
        break;
}

const newContent = content
    .replace(versionCodeRe, `versionCode = ${newVersionCode}`)
    .replace(versionNameRe, `versionName = "${major}.${minor}.${patch}"`);

fs.writeFileSync(appGradle, newContent);

console.log(`Bumped, now run:

git commit -am "release: v${major}.${minor}.${patch}"
git tag v${major}.${minor}.${patch}

git push
git push --tags
  `);
