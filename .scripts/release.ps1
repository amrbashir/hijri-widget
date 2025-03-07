$version = $args[0];

$path = "app/build.gradle.kts"
$appBuild = Get-Content $path;

$appBuild = $appBuild -replace "versionName = `"[0-9]+.[0-9]+.[0-9]+`"", "versionName = `"$version`""

$versionCodeMatches = [regex]::Match($appBuild, 'versionCode = ([0-9]+)')
$currentVersionCode = [int]$versionCodeMatches.Groups[1].Value
$newVersionCode = $currentVersionCode + 1

$appBuild = $appBuild -replace "versionCode = $currentVersionCode", "versionCode = $newVersionCode"

Set-Content -Path $path -Value $appBuild

git add .
git commit -m "release: v$version"
git push
git tag "v$version"
git push --tags
