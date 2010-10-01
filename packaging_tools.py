import zipfile, os, glob, re, sys, new

##
# Conditional expression
IF = lambda a,b,c:(a and [b] or [c])[0]

##
# Update svn repository.
def updateSvnRepository():
	print 'Updating SVN repository' 
	os.system('svn update')
	print

##
# Check for SVN uncommitted changes, ignoring items not under version control
def checkSvnUncommittedChanges():
	print 'Checking for uncommitted changes'
	hasUncommittedChanges = False
	for line in os.popen('svn status'):
		line = line.rstrip('\n\r')
		if line.startswith('?'):
			continue
		print line
		hasUncommittedChanges = True
	if hasUncommittedChanges:
		print
		print 'SVN repository has uncommitted changes - Please commit or revert your changes before running packager'
		sys.exit(1)
	else:
		print 'SVN repository has no uncommitted changes '
	print

##
# Get SVN revision.
def getSvnRevision():
	revision = "unknown"
	for line in os.popen('svn info'):
		if line.startswith("Revision:"):
			revision = line.split(None, 2)[1]
	return revision

##
# Get SVN Last Changed revision.
def getSvnLastChangedRevision(directory):
	revision = "unknown"
	svnCommand = 'svn info %s' % (directory)
	for line in os.popen(svnCommand):
		if line.startswith("Last Changed Rev:"):
			revision = line.split(None, 3)[3]
	return revision

##
# Get jar version in the form "<version>_build_<build_number>"
# @param jarFileName file name of the jar containing a manifest with version information
def getJarVersion(jarFileName):
	sys.path.append(os.getenv('QTASTE_ROOT') + '/kernel/target/qtaste-kernel-deploy.jar')
	from com.qspin.qtaste.util import Version
	version = Version(jarFileName)
	return version.getVersion()
	

##
# Create a zip archive from a list of inclusions and a list of exlusions.
# @param archiveName filename of the zip archive
# @param inclusions list of files or directories to be included in archive,
#                   '*' is used to include only the content of the specified directory,
#                   '**' is used to include recursively the content of the specified directory and all the sub-directories
# @param exclusions list of files or directories to be excluded from archive,
#                   '*' matches any character but the directory separator (name or part of name of directory or file),
#                   '**' matches a path of zero, one or several directories
def createArchive(archiveName, inclusions, exclusions):
	escapedOsSep = re.escape(os.sep)
	exclusionsPatterns = [re.compile('^' + re.escape(s.replace('/', os.sep)).replace(escapedOsSep + r'\*\*', '\2').replace(r'\*', '\1').replace('\1', '[^' + escapedOsSep + ']*').replace('\2', '.*') + '$') for s in exclusions]

	zf = zipfile.ZipFile(archiveName, mode='w', compression=zipfile.ZIP_DEFLATED)
	zf.write = new.instancemethod(__zipFileWriteExecutables, zf, zipfile.ZipFile)
	try:
		for inclusion in inclusions:
			__addToArchive(zf, inclusion, exclusionsPatterns)
	finally:
		print '\nCreated archive ' + archiveName
		zf.close()

##
# Add a file of directory to an archive
# @param zf
# @param path file or directory to be added to archive,
#             '*' is used to include only the content of the specified directory,
#             '**' is used to include recursively the content of the specified directory and all the sub-directories
# @param exclusionsPatterns list of pattern exclusions created from the list of files/directories to be excluded from archive,
def __addToArchive(zf, path, exclusionsPatterns):
	if '*' in path:
		dir = os.path.dirname(path) + '/'
		print 'Adding directory ' + dir
		zf.writestr(zipfile.ZipInfo(dir), '')
		recursive = path.endswith('**')
		if recursive:
			for subPath in glob.glob(path):
				if os.path.isdir(subPath):
					if __matchesPatterns(subPath, exclusionsPatterns):
						print 'Ignoring ' + subPath
					else:
						__addToArchive(zf, subPath + '/**', exclusionsPatterns)
				else:
					__addToArchive(zf, subPath, exclusionsPatterns)
		else:
			for subPath in glob.glob(path):
				if os.path.isfile(subPath):
					__addToArchive(zf, subPath, exclusionsPatterns)
	else:
		if __matchesPatterns(path, exclusionsPatterns):
			print 'Ignoring ' + path
			return
		print 'Adding ' + path
		zf.write(path)

##
# rewriting of the write() method of zipfile, to set executable attribute on files having an executable extension (.sh, .class, .jar)
# + replace stat.S_ISDIR by os.path.isdir because former doesn't work under windows
import struct, binascii, stat, time
try:
    import zlib # We may need its compression method
    crc32 = zlib.crc32
except ImportError:
    zlib = None
    crc32 = binascii.crc32
def __zipFileWriteExecutables(self, filename, arcname=None, compress_type=None):
	"""Put the bytes from filename into the archive under the name
	arcname."""
	if not self.fp:
		raise RuntimeError(
			  "Attempt to write to ZIP archive that was already closed")

	st = os.stat(filename)
	isdir = os.path.isdir(filename)
	mtime = time.localtime(st.st_mtime)
	date_time = mtime[0:6]
	# Create ZipInfo instance to store file information
	if arcname is None:
		arcname = filename
	arcname = os.path.normpath(os.path.splitdrive(arcname)[1])
	while arcname[0] in (os.sep, os.altsep):
		arcname = arcname[1:]
	if isdir:
		arcname += '/'
	zinfo = zipfile.ZipInfo(arcname, date_time)
	zinfo.external_attr = (st[0] & 0xFFFF) << 16L      # Unix attributes

	extension = os.path.splitext(filename)[1]
	if extension in ('.sh', '.class', '.jar'):
		zinfo.create_system = 3  # attributes for Unix-system
		zinfo.external_attr = 0777 << 16L

	if compress_type is None:
		zinfo.compress_type = self.compression
	else:
		zinfo.compress_type = compress_type

	zinfo.file_size = st.st_size
	zinfo.flag_bits = 0x00
	zinfo.header_offset = self.fp.tell()    # Start of header bytes

	self._writecheck(zinfo)
	self._didModify = True

	if isdir:
		zinfo.file_size = 0
		zinfo.compress_size = 0
		zinfo.CRC = 0
		self.filelist.append(zinfo)
		self.NameToInfo[zinfo.filename] = zinfo
		self.fp.write(zinfo.FileHeader())
		return

	fp = open(filename, "rb")
	# Must overwrite CRC and sizes with correct data later
	zinfo.CRC = CRC = 0
	zinfo.compress_size = compress_size = 0
	zinfo.file_size = file_size = 0
	self.fp.write(zinfo.FileHeader())
	if zinfo.compress_type == zipfile.ZIP_DEFLATED:
		cmpr = zlib.compressobj(zlib.Z_DEFAULT_COMPRESSION,
			 zipfile.zlib.DEFLATED, -15)
	else:
		cmpr = None
	while 1:
		buf = fp.read(1024 * 8)
		if not buf:
			break
		file_size = file_size + len(buf)
		CRC = crc32(buf, CRC) & 0xffffffff
		if cmpr:
			buf = cmpr.compress(buf)
			compress_size = compress_size + len(buf)
		self.fp.write(buf)
	fp.close()
	if cmpr:
		buf = cmpr.flush()
		compress_size = compress_size + len(buf)
		self.fp.write(buf)
		zinfo.compress_size = compress_size
	else:
		zinfo.compress_size = file_size
	zinfo.CRC = CRC
	zinfo.file_size = file_size
	# Seek backwards and write CRC and file sizes
	position = self.fp.tell()       # Preserve current position in file
	self.fp.seek(zinfo.header_offset + 14, 0)
	self.fp.write(struct.pack("<LLL", zinfo.CRC, zinfo.compress_size,
		  zinfo.file_size))
	self.fp.seek(position, 0)
	self.filelist.append(zinfo)
	self.NameToInfo[zinfo.filename] = zinfo

##
# Check if a string match a pattern in a list of patterns
# @param s string
# @param patterns
def __matchesPatterns(s, patterns):
	for pattern in patterns:
		if pattern.match(s):
			return True
	return False

##
# Check if script is run on Windows
# @return true if script is run on Windows
def runOnWindows():
	OS = os.environ.get('OS')
	return (OS and (OS.lower().find("windows") > -1))

##
# Command separator (for shell commands)
commandSeparator = IF(runOnWindows(), '&', ';')